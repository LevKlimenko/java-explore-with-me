package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public RequestDto save(Long userId, Long eventId) {
        User user = findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        Status statusReq = Status.PENDING;
        if (requestRepository.findFirstByEventAndUser(event, user) != null) {
            throw new ConflictException("Request already have been added");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Can't create request by event's initiator");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Can't add request for not published event");
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequest())) {
            throw new ConflictException("Already max participant");
        }
        if (!event.getRequestModeration()) {
            statusReq = Status.CONFIRMED;
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(statusReq)
                .build();
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto update(Long userId, Long eventId) {
        return null;
    }

    @Override
    public boolean delete(Long userId, Long requestId) {
        return false;
    }

    private User findUserOrGetThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Event findEventOrGetThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found"));
    }
}
