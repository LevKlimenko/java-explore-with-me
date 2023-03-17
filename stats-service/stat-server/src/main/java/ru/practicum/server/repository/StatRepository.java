package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.Hit;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.server.model.ViewStats(h.app.name,h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 and ?2 " +
            "AND h.uri IN (?3) " +
            "GROUP BY h.app.name, h.uri " +
            "ORDER BY COUNT (DISTINCT h.ip) DESC")
    List<ViewStats> findDistinctViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.server.model.ViewStats(h.app.name,h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 and ?2 " +
            "GROUP BY h.app.name, h.uri " +
            "ORDER BY COUNT (DISTINCT h.ip) DESC")
    List<ViewStats> findDistinctViewsAll(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.server.model.ViewStats(h.app.name,h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 and ?2 " +
            "AND h.uri IN (?3) " +
            "GROUP BY h.app.name, h.uri " +
            "ORDER BY COUNT (h.ip) DESC")
    List<ViewStats> findViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.server.model.ViewStats(h.app.name,h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 and ?2 " +
            "GROUP BY h.app.name, h.uri " +
            "ORDER BY COUNT (h.ip) DESC")
    List<ViewStats> findViewsAll(LocalDateTime start, LocalDateTime end);
}