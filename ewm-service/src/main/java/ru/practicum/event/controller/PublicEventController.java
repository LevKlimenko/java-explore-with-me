package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEventsByUser(@RequestParam(required = false) String text,
                                                                  @RequestParam(required = false) List<Long> categories,
                                                                  @RequestParam(required = false) Boolean paid,
                                                                  @RequestParam(required = false)
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                  LocalDateTime rangeStart,
                                                                  @RequestParam(required = false)
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                  LocalDateTime rangeEnd,
                                                                  @RequestParam(defaultValue = "false")
                                                                  Boolean onlyAvailable,
                                                                  @RequestParam(required = false) String sort,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero
                                                                  int from,
                                                                  @RequestParam(defaultValue = "10") @Positive
                                                                  int size,
                                                                  HttpServletRequest request) {
        List<EventShortDto> events = service.findAllByUserWithParameters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
        log.info("Events for user with parameters have been received");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByIdByUser(@PathVariable Long id, HttpServletRequest request) {
        EventFullDto event = service.findByIdByUser(id, request);
        log.info("Event with id={} for user have been received", id);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}