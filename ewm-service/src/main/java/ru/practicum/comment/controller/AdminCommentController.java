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
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin")
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/comments")
    public ResponseEntity<CommentFullDto> updateTextByAdmin(@RequestParam @Positive Long id,
                                                            @Valid @RequestBody CommentIncomingDto commentIncomingDto) {
        CommentFullDto comment = commentService.updateTextByAdmin(id, commentIncomingDto);
        log.info("Comment id={} was updated by admin", id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PatchMapping("/comments/approve")
    public ResponseEntity<CommentFullDto> approveByAdmin(@RequestParam @Positive Long id) {
        CommentFullDto comment = commentService.approveCommentByAdmin(id);
        log.info("Comment id={} was approved by admin", id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PatchMapping("/comments/cancel")
    public ResponseEntity<CommentFullDto> cancelByAdmin(@RequestParam @Positive Long id) {
        CommentFullDto comment = commentService.cancelCommentByAdmin(id);
        log.info("Comment id={} was canceled by admin", id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<Object> deleteCommentByAdmin(@RequestParam @Positive Long id) {
        commentService.deleteByIdByAdmin(id);
        log.info("Comment id={} was deleted by admin", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comments/events/{id}/moderation")
    public ResponseEntity<List<CommentFullDto>> getAllCommentsForEventOnModeration(@PathVariable @Positive Long id,
                                                                                   @RequestParam(defaultValue = "asc") String direction,
                                                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentFullDto> comments = commentService.getAllCommentsForEventIdOnModeration(id, direction, from, size);
        log.info("Comments for Event id={} have been received", id);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/events/{id}")
    public ResponseEntity<List<CommentFullDto>> getAllCommentsForEvent(@PathVariable @Positive Long id,
                                                                       @RequestParam(defaultValue = "id") String typeSort,
                                                                       @RequestParam(defaultValue = "asc") String direction,
                                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentFullDto> comments = commentService.getAllCommentsForEventId(id, typeSort, direction, from, size);
        log.info("Comments for Event id={} have been received", id);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentFullDto>> getAllCommentsWithStatus(
            @RequestParam(defaultValue = "ON_MODERATION") CommentStatus status,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentFullDto> comments = commentService.getAllCommentsWithStatus(status, direction, from, size);
        log.info("Comments on moderation have been received");
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
