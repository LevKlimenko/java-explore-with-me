package ru.practicum.server.service;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.App;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void save(Hit hit, App app);

    List<ViewStatsDto> findAll(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris);
}