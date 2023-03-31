package ru.practicum.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentIncomingDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final CommentRepository commentRepository;

    @Override
    public CommentFullDto add(Long eventId, Long userId, CommentIncomingDto incomingDto) {
        User user = findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (event.getCommentingClosed()){
            throw new ConflictException("Can't add a comment to an event that is closed for comment");
        }
        Comment comment = new Comment();
        comment.setCreatedOn(LocalDateTime.now());
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(incomingDto.getText());
        if (event.getCommentModeration()){
            comment.setStatus(CommentStatus.ON_MODERATION);
        }else {
            comment.setStatus(CommentStatus.PUBLISHED);
        }
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto updateOwner(Long userId, Long commentId, CommentIncomingDto incomingDto) {
        return null;
    }

    @Override
    public CommentFullDto updateAdmin(Long commentId, CommentIncomingDto incomingDto) {
        return null;
    }

    @Override
    public void delete(Long commentId) {

    }

    @Override
    public CommentFullDto findById(Long commentId) {
        return null;
    }

    @Override
    public List<CommentShortDto> findByOwnerId(Long userId) {
        return null;
    }

    @Override
    public List<CommentShortDto> findByEventId(Long eventId) {
        return null;
    }

    private User findUserOrGetThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Event findEventOrGetThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found"));
    }
}
