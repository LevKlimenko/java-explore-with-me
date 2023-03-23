package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long requesterId);
    List<Request> findByEventIdAndStatus(Long eventId,Status status);
    List<Request> findByIdInAndStatus(List<Long> id, Status status);

}
