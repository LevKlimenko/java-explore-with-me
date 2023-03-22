package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;

import java.lang.reflect.Array;
import java.util.List;

public interface EventService {
    EventFullDto saveByOwner(Long userId, NewEventDto newEventDto);
    List<EventShortDto> eventsByOwner(Long id,int from,int size);
    EventFullDto patch(Long userId, Long eventId, UpdateEventDto updateEventDto);
    EventFullDto getByIdByOwner(Long userId, Long eventId);

}
