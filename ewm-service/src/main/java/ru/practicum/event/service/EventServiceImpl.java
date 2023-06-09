package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.stat.StatClient;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ru.practicum.comment.model.CommentStatus.PUBLISHED;
import static ru.practicum.event.dto.UpdateAdminEventDto.StateAction.PUBLISH_EVENT;
import static ru.practicum.event.dto.UpdateAdminEventDto.StateAction.REJECT_EVENT;
import static ru.practicum.event.dto.UpdateUserEventDto.StateAction.CANCEL_REVIEW;
import static ru.practicum.event.dto.UpdateUserEventDto.StateAction.SEND_TO_REVIEW;
import static ru.practicum.event.model.State.PENDING;
import static ru.practicum.request.model.Status.CONFIRMED;
import static ru.practicum.request.model.Status.REJECTED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "id");
    private static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final StatClient statClient;

    /**
     * Private rules
     */

    @Override
    @Transactional
    public EventFullDtoForUser saveByOwner(Long userId, NewEventDto newEventDto) {
        checkEventDateTimeIsAfter2HoursFromNow(newEventDto.getEventDate());
        User owner = findUserOrGetThrow(userId);
        Category category = findCategoryOrGetThrow(newEventDto.getCategory());
        Event event = new Event(0L, newEventDto.getAnnotation(), category, LocalDateTime.now(),
                newEventDto.getDescription(), newEventDto.getLocation(), newEventDto.getEventDate(), owner,
                newEventDto.isPaid(), newEventDto.getParticipantLimit(), null,
                newEventDto.isRequestModeration(), State.PENDING, newEventDto.getTitle(), newEventDto.isCommentModeration(),
                newEventDto.isCommentingClosed(), 0L, 0L, List.of());
        return EventMapper.toEventFullDtoForUser(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> eventsByOwner(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        findUserOrGetThrow(userId);
        return EventMapper.toListEventShortDto(eventRepository.getAllByInitiatorId(userId, pageable));
    }

    @Transactional
    @Override
    public EventFullDtoForUser updateByOwner(Long userId, Long eventId, UpdateUserEventDto updateEventDto) {
        findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Illegal state for update event. State=" + event.getState());
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("You're not owner for event id=" + eventId);
        }
        event = checkAndUpdateEvent(eventId, updateEventDto);
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction() == SEND_TO_REVIEW) {
                event.setState(PENDING);
            } else if (updateEventDto.getStateAction() == CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else {
                throw new BadRequestException("StateAction is not supported for this argument");
            }
        }
        return EventMapper.toEventFullDtoForUser(event);
    }

    @Override
    public EventFullDtoForUser getByIdByOwner(Long userId, Long eventId) {
        User user = findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ForbiddenException("User with id=" + userId + " not owner for event with id=" + eventId);
        }
        return EventMapper.toEventFullDtoForUser(event);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchRequestByInitiator(Long userId, Long eventId, RequestStatusUpdateDto request) {
        findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only initiator could patch requests");
        }
        Status upStatus = request.getStatus();
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        Long confirmedRequestCount = requestRepository.countRequestByEventIdAndStatusEquals(eventId, CONFIRMED);
        event.setConfirmedRequests(confirmedRequestCount);
        List<Request> requestsPending = requestRepository.findByIdIn(request.getRequestIds());
        for (Request rq : requestsPending) {
            if (requestRepository.findById(rq.getId()).orElseThrow(
                            () -> new NotFoundException("Request with ID=" + rq.getId() + " not found"))
                    .getStatus().equals(CONFIRMED)) {
                throw new ConflictException("You can't change an already accepted request");
            }
            if (!rq.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Event and request don't match");
            }
            if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
                throw new ConflictException("The participant limit has been reached");
            }
            if (upStatus.equals(CONFIRMED)) {
                rq.setStatus(upStatus);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(rq);
            }
            if (upStatus.equals(REJECTED)) {
                rq.setStatus(REJECTED);
                rejectedRequests.add(rq);
            }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(RequestMapper.toListRequestDto(confirmedRequests))
                .rejectedRequests(RequestMapper.toListRequestDto(rejectedRequests))
                .build();
    }

    @Override
    public List<RequestDto> getAllRequestsForEventId(Long userId, Long eventId) {
        findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User with id=" + userId + " not initiator for eventId=" + eventId);
        }
        List<Request> requestsForEvent = requestRepository.findByEventIdAndStatus(eventId, Status.PENDING);
        return RequestMapper.toListRequestDto(requestsForEvent);
    }

    /**
     * Admin rules
     */

    @Transactional
    @Override
    public EventFullDtoForAdmin updateByAdmin(Long eventId, UpdateAdminEventDto updateEventDto) {
        Event event = findEventOrGetThrow(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event with id=" + eventId + " has already been published.You can't change it");
        }
        if (event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Event with id=" + eventId + " has already been canceled");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ConflictException("You can't update the event because the start time is less than 1 hour ");
        }
        event = checkAndUpdateEvent(eventId, updateEventDto);
        if (event.getState() == PENDING) {
            if (updateEventDto.getStateAction() == PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventDto.getStateAction() == REJECT_EVENT) {
                event.setState(State.CANCELED);
                event.setPublishedOn(null);
            }
        }
        findAllCommentsForAdmin(List.of(event));
        return EventMapper.toEventFullDtoForAdmin(event);
    }

    @Transactional
    @Override
    public EventFullDtoForAdmin updateModerationByAdmin(Long eventId, UpdateAdminModerationDto updateDto) {
        Event event = findEventOrGetThrow(eventId);
        if (updateDto.getCommentingClosed() != null) {
            event.setCommentingClosed(updateDto.getCommentingClosed());
        }
        if (updateDto.getCommentModeration() != null) {
            event.setCommentModeration(updateDto.getCommentModeration());
        }
        findAllCommentsForAdmin(List.of(event));
        return EventMapper.toEventFullDtoForAdmin(event);
    }

    @Override
    public List<EventFullDtoForAdmin> findByAdminWithParameters(List<Long> users, List<String> states,
                                                                List<Long> categories,
                                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                                int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        QEvent qEvent = QEvent.event;
        BooleanExpression expression = qEvent.id.isNotNull();
        if (users != null && users.size() > 0) {
            expression = expression.and(qEvent.initiator.id.in(users));
        }
        if (states != null && states.size() > 0) {
            expression = expression.and(qEvent.state.in(states.stream()
                    .map(State::valueOf)
                    .collect(Collectors.toUnmodifiableList())));
        }
        if (categories != null && categories.size() > 0) {
            expression = expression.and(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.loe(rangeEnd));
        }
        List<Event> events = eventRepository.findAll(expression, pageable).getContent();
        findAllCommentsForAdmin(events);
        return events.stream().map(EventMapper::toEventFullDtoForAdmin).collect(Collectors.toList());
    }

    /**
     * Public rules
     */

    @Override
    @Transactional
    public List<EventShortDto> findAllByUserWithParameters(String text, List<Long> categories, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           Boolean onlyAvailable, String sort, int from, int size,
                                                           HttpServletRequest request) {
        Sort sortBy = Sort.unsorted();
        if (sort != null) {
            if (sort.equalsIgnoreCase("EVENT_DATE")) {
                sortBy = Sort.by("eventDate");
            } else if (sort.equalsIgnoreCase("VIEWS")) {
                sortBy = Sort.by("views");
            } else {
                throw new BadRequestException("Field: sort. Error: must be EVENT_DATE or VIEWS. Value:" + sort);
            }
        }
        Pageable pageable = PageRequest.of(from / size, size, sortBy);
        QEvent qEvent = QEvent.event;
        BooleanExpression expression = qEvent.id.isNotNull().and(qEvent.state.eq(State.PUBLISHED));
        if (text != null && !text.isEmpty()) {
            expression = expression.and((qEvent.annotation.containsIgnoreCase(text))
                    .or(qEvent.description.containsIgnoreCase(text)));
        }
        if (categories != null && categories.size() > 0) {
            expression = expression.and(qEvent.category.id.in(categories));
        }
        if (paid != null) {
            expression = expression.and(qEvent.paid.eq(paid));
        }
        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.loe(rangeEnd));
        }
        List<Event> events = eventRepository.findAll(expression, pageable).getContent();
        findConfirmedRequest(events);
        if (onlyAvailable) {
            events = events.stream().filter(event ->
                    event.getParticipantLimit() > event.getConfirmedRequests()).collect(Collectors.toList());
        }
        findConfirmedComments(events);
        findViews(events);
        statClient.saveHit(request);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDtoForUser findByIdByUser(Long id, HttpServletRequest request) {
        Event event = findEventOrGetThrow(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Event with id=" + id + " not published");
        }
        findViews(List.of(event));
        findConfirmedComments(List.of(event));
        findConfirmedRequest(List.of(event));
        statClient.saveHit(request);
        return EventMapper.toEventFullDtoForUser(event);
    }

    private User findUserOrGetThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Category findCategoryOrGetThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id=" + categoryId + " not found"));
    }

    private Event findEventOrGetThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found"));
    }

    private void checkEventDateTimeIsAfter2HoursFromNow(LocalDateTime time) {
        if (time.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event time should be after 2 hours from now");
        }
    }

    private void findConfirmedRequest(List<Event> events) {
        Map<Event, Long> requests = requestRepository.findAllByEventInAndStatusEquals(events, CONFIRMED)
                .stream()
                .collect(groupingBy(Request::getEvent, counting()));
        events.forEach(event -> event.setConfirmedRequests(requests.get(event)));
    }

    private void findConfirmedComments(List<Event> events) {
        Map<Event, List<Comment>> comments =
                commentRepository.getAllByEventInAndStatusEquals(events, PUBLISHED)
                        .stream()
                        .collect(groupingBy(Comment::getEvent));
        events.forEach(event -> event.setComments(comments.getOrDefault(event, List.of())));
    }

    private void findAllCommentsForAdmin(List<Event> events) {
        Map<Event, List<Comment>> comments =
                commentRepository.getAllByEventInAndStatusNot(events, CommentStatus.CANCELED)
                        .stream()
                        .collect(groupingBy(Comment::getEvent));
        events.forEach(event -> event.setComments(comments.getOrDefault(event, List.of())));
    }

    private void findViews(List<Event> events) {
        Set<Long> eventsId = events.stream().map(Event::getId).collect(Collectors.toSet());
        String start = timeToString(events.stream()
                .min(Comparator.comparing(Event::getCreatedOn)).get().getCreatedOn());
        String end = timeToString(LocalDateTime.now());
        Map<Long, Long> views = statClient.getViews(eventsId, start, end).stream()
                .collect(Collectors.toMap(v -> Long.parseLong(v.getUri().substring(8)), ViewStatsDto::getHits));
        events.forEach(event -> event.setViews(views.getOrDefault(event.getId(), 0L)));
    }

    private Event checkAndUpdateEvent(Long eventId, UpdateEventDto upEventDto) {
        Event event = findEventOrGetThrow(eventId);
        if (upEventDto.getAnnotation() != null && !upEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(upEventDto.getAnnotation());
        }
        if (upEventDto.getCategory() != null) {
            event.setCategory(findCategoryOrGetThrow(upEventDto.getCategory()));
        }
        if (upEventDto.getDescription() != null && !upEventDto.getDescription().isBlank()) {
            event.setDescription(upEventDto.getDescription());
        }
        if (upEventDto.getEventDate() != null) {
            checkEventDateTimeIsAfter2HoursFromNow(upEventDto.getEventDate());
            event.setEventDate(upEventDto.getEventDate());
        }
        if (upEventDto.getLocation() != null) {
            event.setLocation(upEventDto.getLocation());
        }
        if (upEventDto.getPaid() != null) {
            event.setPaid(upEventDto.getPaid());
        }
        if (upEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(upEventDto.getParticipantLimit());
        }
        if (upEventDto.getRequestModeration() != null) {
            event.setRequestModeration(upEventDto.getRequestModeration());
        }
        if (upEventDto.getTitle() != null && !upEventDto.getTitle().isBlank()) {
            event.setTitle(upEventDto.getTitle());
        }
        if (upEventDto.getCommentingClosed() != null) {
            event.setCommentingClosed(upEventDto.getCommentingClosed());
        }
        if (upEventDto.getCommentModeration() != null) {
            event.setCommentModeration(upEventDto.getCommentModeration());
        }
        return event;
    }

    private String timeToString(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}