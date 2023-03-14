package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDtoRequest;
import ru.practicum.dto.HitMapper;
import ru.practicum.dto.ViewStats;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;


    @Override
    public void save(HitDtoRequest hitDtoRequest) {
        statRepository.save(HitMapper.toHit(hitDtoRequest));
    }

    @Override
    public List<ViewStats> findAll(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris) {
        if (unique) {
            return statRepository.findDistinctViews(start, end, uris);
        } else return statRepository.findViews(start, end, uris);
    }
}
