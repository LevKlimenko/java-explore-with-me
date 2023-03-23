package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto saveByOwner(Long userId, NewEventDto newEventDto);
    List<EventShortDto> eventsByOwner(Long id,int from,int size);
    EventFullDto updateByOwner(Long userId, Long eventId, UpdateEventDto updateEventDto);
    EventFullDto getByIdByOwner(Long userId, Long eventId);

    EventFullDto updateByAdmin(Long eventId, UpdateEventDto updateEventDto);

    List<EventFullDto> findByAdminWithParameters(List<Long> users, List<String> states, List<Long> categories,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventFullDto> findAllByUserWithParameters(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable, String sort, int from, int size,
                                                   HttpServletRequest request);

    EventFullDto findByIdByUser(Long id, HttpServletRequest request);

    List<RequestDto> requestsPatch(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<RequestDto> getAllRequestsForEventId(Long userId, Long eventId);

}
