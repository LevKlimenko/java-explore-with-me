package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentIncomingDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PrivateCommentController {
    private final CommentService service;

    @PostMapping("/events/{eventId}/user/{userId}/comments")
    public ResponseEntity<CommentFullDto> add(@PathVariable @Positive Long eventId,
                                              @PathVariable @Positive Long userId,
                                              @Valid @RequestBody CommentIncomingDto commentIncomingDto) {
        CommentFullDto comment = service.add(eventId, userId, commentIncomingDto);
        log.info("Comment from userId={} to eventId={} have been added", userId, eventId);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentFullDto> update(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long commentId,
                                                 @Valid @RequestBody CommentIncomingDto commentIncomingDto) {
        CommentFullDto comment = service.updateByOwner(userId, commentId, commentIncomingDto);
        log.info("Comment id={} from userId={} have been updated", commentId, userId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        service.delete(userId, commentId);
        log.info("Comment id={} from userId={} have been deleted", commentId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<CommentFullDto>> getAllByOwner(@PathVariable @Positive Long userId,
                                                              @RequestParam(defaultValue = "id") String sortBy,
                                                              @RequestParam(defaultValue = "asc") String direction,
                                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                              @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentFullDto> comments = service.findByOwnerId(userId, sortBy, direction, from, size);
        log.info("Comments for User id={} have been received", userId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
