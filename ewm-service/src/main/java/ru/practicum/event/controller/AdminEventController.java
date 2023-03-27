package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> eventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                            @RequestParam(required = false) List<String> states,
                                                            @RequestParam(required = false) List<Long> categories,
                                                            @RequestParam(required = false)
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                            @RequestParam(required = false)
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "10") @Positive int size) {
        List<EventFullDto> events = service.findByAdminWithParameters(users, states, categories, rangeStart, rangeEnd,
                from, size);
        log.info("Events for admin with parameters have been received");
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> patchEventByAdmin(@PathVariable Long eventId, @RequestBody UpdateEventDto updateEventDto) {
        EventFullDto event = service.updateByAdmin(eventId, updateEventDto);
        log.info("Event with Id={}  have been updated by Admin", eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}