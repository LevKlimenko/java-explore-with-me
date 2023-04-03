package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.event.model.Event;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> getAllByEventInAndStatusEquals(List<Event> ids, CommentStatus status);

    List<Comment> getAllByEventIn(List<Event> ids);

    List<Comment> getAllByUserId(Long userId, Pageable pageable);

    List<Comment> getAllByStatusEquals(CommentStatus status, Pageable pageable);

    List<Comment> getAllByUserIdAndStatusEquals(Long userId, CommentStatus status, Pageable pageable);

    List<Comment> getAllByEventIdAndStatusEquals(Long eventId, CommentStatus status, Pageable pageable);

    List<Comment> getAllByEventId(Long eventId, Pageable pageable);
}