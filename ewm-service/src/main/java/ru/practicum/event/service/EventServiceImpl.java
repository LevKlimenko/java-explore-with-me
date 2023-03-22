package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "id");
    private static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    /**
     * Private rules
     */

    @Override
    @Transactional
    public EventFullDto saveByOwner(Long userId, NewEventDto newEventDto) {
        checkEventDateTimeIsAfter2HoursFromNow(newEventDto.getEventDate());
        User owner = findUserOrGetThrow(userId);
        Category category = findCategoryOrGetThrow(newEventDto.getCategory());
        Event event = new Event(0L, newEventDto.getAnnotation(), category, 0L, LocalDateTime.now(),
                newEventDto.getDescription(), newEventDto.getLocation(), newEventDto.getEventDate(), owner,
                newEventDto.getPaid(), newEventDto.getParticipantLimit(), null, newEventDto.getRequestModeration(),
                State.PENDING, newEventDto.getTitle(), 0L);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> eventsByOwner(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        findUserOrGetThrow(userId);
        return EventMapper.toListEventShortDto(eventRepository.getAllByInitiatorId(userId, pageable));
    }

    @Transactional
    @Override
    public EventFullDto patch(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("You're not owner for event id=" + eventId);
        }
        State state = event.getState();
        if (state != State.CANCELED && state != State.PENDING) {
            throw new ConflictException("Illegal state for update event. state=" + state);
        }
        return EventMapper.toEventFullDto(checkAndUpdateEvent(eventId, updateEventDto));
    }

    @Override
    public EventFullDto getByIdByOwner(Long userId, Long eventId) {
        User user = findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ForbiddenException("User with id=" + userId + " not owner for event with id=" + eventId);
        }
        return EventMapper.toEventFullDto(event);
    }

    /**
     * Admin rules
     */

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
            throw new ForbiddenException("Event time should be after 2 hours from now");
        }
    }

    private Event checkAndUpdateEvent(Long eventId, UpdateEventDto upEventDto) {
        Event event = findEventOrGetThrow(eventId);
        if (!upEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(upEventDto.getAnnotation());
        }
        if (upEventDto.getCategory() != null) {
            event.setCategory(findCategoryOrGetThrow(upEventDto.getCategory()));
        }
        if (!upEventDto.getDescription().isBlank()) {
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
        if (upEventDto.getStateAction() != null) {
            if (upEventDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            } else if (upEventDto.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else {
                throw new BadRequestException("StateAction is not supported for this argument");
            }
        }
        if (upEventDto.getTitle() != null) {
            event.setTitle(upEventDto.getTitle());
        }
        return event;
    }
}
