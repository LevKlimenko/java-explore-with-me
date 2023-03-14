package ru.practicum.service;

import ru.practicum.dto.HitDtoRequest;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void save(HitDtoRequest hitDtoRequest);

    List<ViewStats> findAll(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris);
}
