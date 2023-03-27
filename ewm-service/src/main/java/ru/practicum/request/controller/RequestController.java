package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    public ResponseEntity<RequestDto> create(@PathVariable Long userId, @RequestParam Long eventId) {
        RequestDto requestDto = requestService.save(userId, eventId);
        log.info("Request from userId={} to eventId={} have been added", userId, eventId);
        return new ResponseEntity<>(requestDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getAllByUserId(@PathVariable Long userId) {
        List<RequestDto> requests = requestService.requestsForUser(userId);
        log.info("Request for userId={} have been received", userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequestById(@PathVariable Long userId, @PathVariable Long requestId) {
        RequestDto request = requestService.cancel(userId, requestId);
        log.info("Request for userId={} for requestId={} have been canceled", userId, requestId);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}