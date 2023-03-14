package ru.practicum.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StatServerRepositoryTest {
    private final LocalDateTime START = LocalDateTime.of(2023, 3, 14, 9, 0, 0);
    private final LocalDateTime END = LocalDateTime.of(2023, 3, 20, 10, 0, 0);
    @Autowired
    StatRepository statRepository;

    @Test
    public void findDistinctViewsTestOk() {
        Hit hit = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(START.plusSeconds(1))
                .build();
        Hit hit1 = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(END.minusSeconds(1))
                .build();
        statRepository.save(hit);
        statRepository.save(hit1);
        List<ViewStats> stats = statRepository.findDistinctViews(START, END, List.of("/events/1"));
        assertEquals(1, stats.size());
        assertEquals("new app", stats.get(0).getApp());
        assertEquals("/events/1", stats.get(0).getUri());
        assertEquals(1, stats.get(0).getHits());
    }

    @Test
    public void findTwoDistinctHitViewsTestOk() {
        Hit hit = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.2")
                .timestamp(START.plusSeconds(1))
                .build();
        Hit hit1 = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(END.minusSeconds(1))
                .build();
        statRepository.save(hit);
        statRepository.save(hit1);
        List<ViewStats> stats = statRepository.findDistinctViews(START, END, List.of("/events/1"));
        assertEquals(1, stats.size());
        assertEquals("new app", stats.get(0).getApp());
        assertEquals("/events/1", stats.get(0).getUri());
        assertEquals(2, stats.get(0).getHits());
    }


    @Test
    public void findViewsTestOk() {
        Hit hit = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(START.plusSeconds(1))
                .build();
        Hit hit1 = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(END.minusSeconds(1))
                .build();
        statRepository.save(hit);
        statRepository.save(hit1);
        List<ViewStats> stats = statRepository.findViews(START, END, List.of("/events/1"));
        assertEquals(1, stats.size());
        assertEquals("new app", stats.get(0).getApp());
        assertEquals("/events/1", stats.get(0).getUri());
        assertEquals(2, stats.get(0).getHits());
    }

    @Test
    public void findViewsWithOverStartAndEndIsEmpty() {
        Hit hit = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(START.minusSeconds(1))
                .build();
        Hit hit1 = Hit.builder()
                .app("new app")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(END.plusSeconds(1))
                .build();
        statRepository.save(hit);
        statRepository.save(hit1);
        List<ViewStats> stats = statRepository.findViews(START, END, List.of("/events/1"));
        assertEquals(0, stats.size());
    }
}
