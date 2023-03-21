package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;

import java.lang.reflect.Array;
import java.util.List;

public interface EventService {
    EventFullDto saveByOwner(Long userId, NewEventDto newEventDto);
    List<EventFullDto> eventsByOwner(Long id,int from,int size);
    EventFullDto patch(Long userId, NewEventDto updateEventDto);
    EventFullDto getByIdByOwner(Long userId, Long eventId);

}
