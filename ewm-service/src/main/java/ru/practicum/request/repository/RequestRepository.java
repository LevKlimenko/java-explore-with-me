package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findFirstByEventAndUser(Event event, User user);
}
