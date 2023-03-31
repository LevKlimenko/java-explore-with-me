package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.event.model.Event;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> getAllByEventInAndStatusEquals (List<Event> ids, CommentStatus status);
}
