package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDtoForUser saveByOwner(Long userId, NewEventDto newEventDto);

    List<EventShortDto> eventsByOwner(Long id, int from, int size);

    EventFullDtoForUser updateByOwner(Long userId, Long eventId, UpdateUserEventDto updateEventDto);

    EventFullDtoForUser getByIdByOwner(Long userId, Long eventId);

    EventFullDtoForAdmin updateByAdmin(Long eventId, UpdateAdminEventDto updateEventDto);

    EventFullDtoForAdmin updateModerationByAdmin(Long eventId, UpdateAdminModerationDto updateDto);

    List<EventFullDtoForAdmin> findByAdminWithParameters(List<Long> users, List<String> states, List<Long> categories,
                                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> findAllByUserWithParameters(String text, List<Long> categories, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    Boolean onlyAvailable, String sort, int from, int size,
                                                    HttpServletRequest request);

    EventFullDtoForUser findByIdByUser(Long id, HttpServletRequest request);

    EventRequestStatusUpdateResult patchRequestByInitiator(Long userId, Long eventId, RequestStatusUpdateDto request);

    List<RequestDto> getAllRequestsForEventId(Long userId, Long eventId);
}