package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> getAllByInitiatorId(Long id, Pageable pageable);

    Optional<Event> getEventByCategoryId(Long id);

    Set<Event> getEventByIdIn(Set<Long> eventsId);
}
