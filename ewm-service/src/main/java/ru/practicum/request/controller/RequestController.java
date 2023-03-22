package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> create(@PathVariable Long userId, @RequestParam Long eventId) {
        RequestDto requestDto = requestService.save(userId, eventId);
        log.info("Request from userId={} to eventId={} have been added", userId, eventId);
        return new ResponseEntity<>(requestDto, HttpStatus.CREATED);
    }
}
