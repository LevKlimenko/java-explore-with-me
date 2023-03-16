package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.HitMapper;
import ru.practicum.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void save(HitRequestDto hitDtoRequest) {
        statRepository.save(HitMapper.toHit(hitDtoRequest));
    }

    @Override
    public List<ViewStatsDto> findAll(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris) {
        if (unique) {
            return statRepository.findDistinctViews(start, end, uris).stream().map(HitMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        } else return statRepository.findViews(start, end, uris).stream().map(HitMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }
}