package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentIncomingDto;
import ru.practicum.comment.dto.CommentShortDto;

import java.util.List;

public interface CommentService {
    CommentFullDto add( Long eventId, Long userId, CommentIncomingDto incomingDto);

    CommentFullDto updateOwner(Long userId, Long commentId, CommentIncomingDto incomingDto);

    CommentFullDto updateAdmin(Long commentId, CommentIncomingDto incomingDto);

    void delete(Long commentId);

    CommentFullDto findById(Long commentId);

    List<CommentShortDto> findByOwnerId(Long userId);

    List<CommentShortDto> findByEventId(Long eventId);

}
