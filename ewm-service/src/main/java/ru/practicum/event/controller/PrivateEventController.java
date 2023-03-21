package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> saveByOwner(@PathVariable Long userId,
                                              @Valid @RequestBody NewEventDto newEventDto){
        EventFullDto event = eventService.saveByOwner(userId,newEventDto);
        log.info("Event from userId={} have been added",userId);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }
}
