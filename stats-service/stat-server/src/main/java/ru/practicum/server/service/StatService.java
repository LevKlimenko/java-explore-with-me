package ru.practicum.server.service;

import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void save(HitRequestDto hitDtoRequest);

    List<ViewStatsDto> findAll(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris);
}