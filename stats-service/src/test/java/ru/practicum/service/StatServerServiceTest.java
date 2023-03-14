package ru.practicum.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.HitDtoRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatServerServiceTest {
    @InjectMocks
    StatServiceImpl statService;
    @Mock
    StatRepository statRepository;

    @Test
    void saveOk() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .app("app")
                .uri("uri")
                .ip("192.168.0.1")
                .timestamp("2023-03-14 14:00:00")
                .build();
        when(statRepository.save(any())).thenReturn(any());
        statService.save(hit);
        verify(statRepository).save(any());
    }

    @Test
    void findDistinctViewsOk() {
        ViewStats viewStats = new ViewStats("app", "uri", 1L);
        List<ViewStats> viewStatsList = List.of(viewStats);
        when(statRepository.findDistinctViews(any(), any(), anyList())).thenReturn(viewStatsList);
        List<ViewStats> actualViewStatsList = statService.findAll(
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), true, List.of());
        assertEquals(viewStatsList, actualViewStatsList);
    }

    @Test
    void findNotDistinctViewsOk() {
        ViewStats viewStats = new ViewStats("app", "uri", 1L);
        List<ViewStats> viewStatsList = List.of(viewStats);
        when(statRepository.findViews(any(), any(), anyList())).thenReturn(viewStatsList);
        List<ViewStats> actualViewStatsList = statService.findAll(
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), false, List.of());
        assertEquals(viewStatsList, actualViewStatsList);
    }
}
