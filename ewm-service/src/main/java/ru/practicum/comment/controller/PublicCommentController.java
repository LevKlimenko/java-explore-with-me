package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PublicCommentController {
    private final CommentService service;

    @GetMapping("/comments/users/{userId}")
    public ResponseEntity<List<CommentShortDto>> findByOwnerIdFromUser(@PathVariable @Positive Long userId,
                                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction,
                                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentShortDto> comments = service.findByOwnerIdFromUser(userId, sortBy, direction, from, size);
        log.info("Comments for User id={} have been received", userId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/events/{eventId}")
    public ResponseEntity<List<CommentShortDto>> findByEventIdFromUser(@PathVariable @Positive Long eventId,
                                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction,
                                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentShortDto> comments = service.findByEventIdFromUser(eventId, sortBy, direction, from, size);
        log.info("Comments for Event id={} have been received", eventId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<CommentFullDto> findByIdFromUser(@RequestParam @Positive Long id) {
        CommentFullDto comment = service.findByIdFromUser(id);
        log.info("Comment with id={} have been received", id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}