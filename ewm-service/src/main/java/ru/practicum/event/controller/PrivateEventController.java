package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;

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
    public ResponseEntity<EventFullDto> saveByOwner(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) {
        EventFullDto event = eventService.saveByOwner(userId, newEventDto);
        log.info("Event from userId={} with eventId={} have been added", userId, event.getId());
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getAllByOwner(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                             @RequestParam(defaultValue = "10") @Positive int size) {
        List<EventShortDto> events = eventService.eventsByOwner(userId, from, size);
        log.info("Events for user id={} have been received", userId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getById(@PathVariable Long userId, @PathVariable Long eventId) {
        EventFullDto event = eventService.getByIdByOwner(userId, eventId);
        log.info("Event with Id={} for user id={} have been received", eventId, userId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> patchByOwnerWithId(@PathVariable Long userId, @PathVariable Long eventId,
                                                           @Valid @RequestBody UpdateUserEventDto updateEventDto) {
        EventFullDto event = eventService.updateByOwner(userId, eventId, updateEventDto);
        log.info("Event with Id={} for user id={} have been updated", eventId, userId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> requestsPatch(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @RequestBody RequestStatusUpdateDto request) {
        EventRequestStatusUpdateResult requests = eventService.patchRequestByInitiator(userId, eventId, request);
        log.info("Requests have been updated");
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> requestsForEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        List<RequestDto> requests = eventService.getAllRequestsForEventId(userId, eventId);
        log.info("Requests have been received");
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }
}