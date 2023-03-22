package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

public interface RequestService {
    RequestDto save(Long userId, Long eventId);
    RequestDto update(Long userId, Long eventId);
    boolean delete(Long userId,Long requestId);
}
