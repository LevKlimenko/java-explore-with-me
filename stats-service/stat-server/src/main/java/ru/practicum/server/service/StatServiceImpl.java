package ru.practicum.server.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.StatMapper;
import ru.practicum.server.model.App;
import ru.practicum.server.model.Hit;
import ru.practicum.server.repository.AppRepository;
import ru.practicum.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatServiceImpl implements StatService {
    final StatRepository statRepository;
    final AppRepository appRepository;


    @Override
    @Transactional
    public void save(Hit hit, App app) {
        hit.setApp(Objects.requireNonNullElseGet(appRepository.findByName(app.getName()),
                () -> appRepository.save(app)));
        statRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> findAll(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris) {
        if (unique) {
            if (uris == null || uris.size() == 0) {
                return statRepository.findDistinctViewsAll(start, end).stream().map(StatMapper::toViewStatsDto).collect(Collectors.toList());
            } else {
                return statRepository.findDistinctViews(start, end, uris).stream().map(StatMapper::toViewStatsDto).collect(Collectors.toList());
            }
        } else {
            if (uris == null || uris.size() == 0) {
                return statRepository.findViewsAll(start, end).stream().map(StatMapper::toViewStatsDto).collect(Collectors.toList());
            } else {
                return statRepository.findViews(start, end, uris).stream().map(StatMapper::toViewStatsDto).collect(Collectors.toList());
            }
        }
    }
}