package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentIncomingDto;
import ru.practicum.comment.service.CommentService;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class PrivateCommentController {
    private final CommentService service;

    @PostMapping("/{eventId}/user/{userId}/comment")
    public ResponseEntity<CommentFullDto> add(@PathVariable Long eventId, @PathVariable Long userId,
                                              CommentIncomingDto commentIncomingDto) {
        CommentFullDto comment = service.add(eventId, userId, commentIncomingDto);
        log.info("Comment from userId={} to eventId={} have been added", userId, eventId);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }
}
