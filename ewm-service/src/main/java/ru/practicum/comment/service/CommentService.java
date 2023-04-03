package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentIncomingDto;
import ru.practicum.comment.dto.CommentShortDto;

import java.util.List;

public interface CommentService {
    CommentFullDto add(Long eventId, Long userId, CommentIncomingDto incomingDto);

    CommentFullDto updateByOwner(Long userId, Long commentId, CommentIncomingDto incomingDto);

    CommentFullDto updateTextByAdmin(Long commentId, CommentIncomingDto incomingDto);

    List<CommentFullDto> findByOwnerId(Long userId, String typeSort, String directionSort, int from, int size);

    void deleteByIdByAdmin(Long commentId);

    void delete(Long userId, Long commentId);

    CommentFullDto approveCommentByAdmin(Long commentId);

    List<CommentFullDto> getAllCommentsForEventIdOnModeration(Long eventId, String directSort, int from, int size);

    List<CommentFullDto> getAllCommentsForEventId(Long eventId, String typeSort, String directionSort,
                                                  int from, int size);

    List<CommentFullDto> getAllCommentsOnModeration(String directSort, int from, int size);

    List<CommentShortDto> findByOwnerIdFromUser(Long userId, String typeSort,
                                                String directionSort, int from, int size);

    List<CommentShortDto> findByEventIdFromUser(Long eventId, String typeSort,
                                                String directionSort, int from, int size);

    CommentFullDto findByIdFromUser(Long commentId);
}
