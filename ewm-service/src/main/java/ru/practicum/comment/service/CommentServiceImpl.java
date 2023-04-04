package ru.practicum.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.event.model.State;
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

    /**
     * User Controller
     */

    @Transactional
    @Override
    public CommentFullDto add(Long eventId, Long userId, CommentIncomingDto incomingDto) {
        User user = findUserOrGetThrow(userId);
        Event event = findEventOrGetThrow(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Can't add a comment to an event that is not published");
        }
        if (event.getCommentingClosed()) {
            throw new ConflictException("Can't add a comment to an event that is closed for comment");
        }
        Comment comment = new Comment();
        comment.setCreatedOn(LocalDateTime.now());
        comment.setLastUpdate(LocalDateTime.now());
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(incomingDto.getText());
        if (event.getCommentModeration()) {
            comment.setStatus(CommentStatus.ON_MODERATION);
        } else {
            comment.setStatus(CommentStatus.PUBLISHED);
        }
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentFullDto updateByOwner(Long userId, Long commentId, CommentIncomingDto incomingDto) {
        User user = findUserOrGetThrow(userId);
        Comment comment = findCommentOrGetThrow(commentId);
        if (!comment.getUser().equals(user)) {
            throw new ConflictException("You're not owner for comment id=" + commentId);
        }
        Event event = comment.getEvent();
        if (event.getCommentingClosed()) {
            throw new ConflictException("You can't change a comment in an event that is closed for comment");
        }
        if (LocalDateTime.now().isAfter(comment.getCreatedOn().plusMinutes(1440))) {
            throw new ConflictException("You can't change a comment after 24h after creation");
        }
        if (event.getCommentModeration()) {
            comment.setStatus(CommentStatus.ON_MODERATION);
        }
        comment.setText(incomingDto.getText());
        comment.setLastUpdate(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long commentId) {
        Comment comment = findCommentOrGetThrow(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("You're not owner for comment id=" + commentId);
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentFullDto> findByOwnerId(Long userId, String typeSort, String directionSort, int from, int size) {
        findUserOrGetThrow(userId);
        Pageable pageable = getSortFromParam(typeSort, directionSort, from, size);
        return CommentMapper.toCommentFullDtoList(commentRepository.getAllByUserId(userId, pageable));
    }

    /**
     * Admin controller
     */

    @Transactional
    @Override
    public void deleteByIdByAdmin(Long commentId) {
        findCommentOrGetThrow(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public CommentFullDto updateTextByAdmin(Long commentId, CommentIncomingDto incomingDto) {
        Comment comment = findCommentOrGetThrow(commentId);
        comment.setText(incomingDto.getText());
        comment.setLastUpdate(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    @Override
    public CommentFullDto approveCommentByAdmin(Long commentId) {
        Comment comment = findCommentOrGetThrow(commentId);
        if (comment.getStatus().equals(CommentStatus.PUBLISHED)) {
            throw new ConflictException("The comment has already been published");
        }
        comment.setStatus(CommentStatus.PUBLISHED);
        comment.setLastUpdate(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    @Override
    public CommentFullDto cancelCommentByAdmin(Long commentId) {
        Comment comment = findCommentOrGetThrow(commentId);
        comment.setStatus(CommentStatus.CANCELED);
        comment.setLastUpdate(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    public List<CommentFullDto> getAllCommentsForEventIdOnModeration(Long eventId, String directSort, int from, int size) {
        findEventOrGetThrow(eventId);
        String sortedBy = "date";
        Pageable pageable = getSortFromParam(sortedBy, directSort, from, size);
        return CommentMapper.toCommentFullDtoList(
                commentRepository.getAllByEventIdAndStatusEquals(eventId, CommentStatus.ON_MODERATION, pageable));
    }

    @Override
    public List<CommentFullDto> getAllCommentsForEventId(Long eventId, String typeSort, String directionSort,
                                                         int from, int size) {
        findEventOrGetThrow(eventId);
        Pageable pageable = getSortFromParam(typeSort, directionSort, from, size);
        return CommentMapper.toCommentFullDtoList(commentRepository.getAllByEventId(eventId, pageable));
    }

    @Override
    public List<CommentFullDto> getAllCommentsWithStatus(CommentStatus status, String directSort, int from, int size) {
        String sortedBy = "date";
        Pageable pageable = getSortFromParam(sortedBy, directSort, from, size);
        return CommentMapper.toCommentFullDtoList(
                commentRepository.getAllByStatusEquals(status, pageable));
    }

    /**
     * Public controller
     */

    @Override
    public List<CommentShortDto> findByOwnerIdFromUser(Long userId, String typeSort, String directionSort,
                                                       int from, int size) {
        findUserOrGetThrow(userId);
        Pageable pageable = getSortFromParam(typeSort, directionSort, from, size);
        return CommentMapper.toCommentShortDtoList(
                commentRepository.getAllByUserIdAndStatusEquals(userId, CommentStatus.PUBLISHED, pageable));
    }

    @Override
    public List<CommentShortDto> findByEventIdFromUser(Long eventId, String typeSort, String directionSort,
                                                       int from, int size) {
        findEventOrGetThrow(eventId);
        Pageable pageable = getSortFromParam(typeSort, directionSort, from, size);
        return CommentMapper.toCommentShortDtoList(
                commentRepository.getAllByEventIdAndStatusEquals(eventId, CommentStatus.PUBLISHED, pageable));
    }

    @Override
    public CommentFullDto findByIdFromUser(Long commentId) {
        Comment comment = findCommentOrGetThrow(commentId);
        if (comment.getStatus().equals(CommentStatus.ON_MODERATION)) {
            throw new ConflictException("Comment with id=" + commentId + " is under moderation");
        }
        return CommentMapper.toCommentFullDto(comment);
    }

    private User findUserOrGetThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Event findEventOrGetThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found"));
    }

    private Comment findCommentOrGetThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with id=" + commentId + " not found"));
    }

    private Pageable getSortFromParam(String typeSort, String directionSort, int from, int size) {
        Sort sort;
        if (typeSort.equalsIgnoreCase("date")) {
            if (directionSort.equalsIgnoreCase("desc")) {
                sort = Sort.by(Sort.Direction.DESC, "createdOn");
            } else {
                sort = Sort.by(Sort.Direction.ASC, "createdOn");
            }
        } else if (typeSort.equalsIgnoreCase("lastUpdate")) {
            if (directionSort.equalsIgnoreCase("desc")) {
                sort = Sort.by(Sort.Direction.DESC, "lastUpdate");
            } else {
                sort = Sort.by(Sort.Direction.ASC, "lastUpdate");
            }
        } else {
            if (directionSort.equalsIgnoreCase("desc")) {
                sort = Sort.by(Sort.Direction.DESC, "id");
            } else {
                sort = Sort.by(Sort.Direction.ASC, "id");
            }
        }
        return PageRequest.of(from / size, size, sort);
    }
}