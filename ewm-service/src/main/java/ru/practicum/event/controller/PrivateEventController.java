package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> saveByOwner(@PathVariable Long userId,
                                              @Valid @RequestBody NewEventDto newEventDto) {
        EventFullDto event = eventService.saveByOwner(userId, newEventDto);
        log.info("Event from userId={} with eventId={} have been added", userId,event.getId());
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getAllByOwner(@PathVariable Long userId,
                                                @Valid @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @Valid @RequestParam(defaultValue = "10") @Positive int size){
        List<EventShortDto> events = eventService.eventsByOwner(userId,from,size);
        log.info("Events for user id={} have been received", userId);
        return new ResponseEntity<>(events,HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId, @PathVariable Long eventId){
        EventFullDto event = eventService.getByIdByOwner(userId, eventId);
        log.info("Event with Id={} for user id={} have been received",eventId, userId);
        return new ResponseEntity<>(event,HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> patchByOwnerWithId(@PathVariable Long userId, @PathVariable Long eventId,
                                                     @RequestBody(required = false) UpdateEventDto updateEventDto){
        EventFullDto event = eventService.patch(userId, eventId, updateEventDto);
        log.info("Event with Id={} for user id={} have been updated",eventId, userId);
        return new ResponseEntity<>(event,HttpStatus.OK);
    }
}
