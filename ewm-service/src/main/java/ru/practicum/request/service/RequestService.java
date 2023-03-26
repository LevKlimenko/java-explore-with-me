package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto save(Long userId, Long eventId);

    RequestDto cancel(Long userId, Long requestId);

    List<RequestDto> requestsForUser(Long userId);
}
