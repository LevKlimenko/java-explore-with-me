package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventFullDto saveByOwner(Long userId, NewEventDto newEventDto) {
        if ( newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))){
            throw new ForbiddenException("Event time is bad");
        }
        User owner = userRepository.findById(userId).orElseThrow(
                ()-> new NotFoundException("User with id="+userId+ " not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                ()-> new NotFoundException("Category with id="+newEventDto.getCategory()+ " not found")
        );
        Event event = new Event(0L, newEventDto.getAnnotation(), category, 0L,LocalDateTime.now(),
                newEventDto.getDescription(), newEventDto.getLocation(),newEventDto.getEventDate(),owner,
                newEventDto.isPaid(), newEventDto.getParticipantLimit(), null, newEventDto.isRequestModeration(),
                State.PENDING, newEventDto.getTitle(),0L);
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> eventsByOwner(Long id, int from, int size) {
        return null;
    }

    @Override
    public EventFullDto patch(Long userId, NewEventDto updateEventDto) {
        return null;
    }

    @Override
    public EventFullDto getByIdByOwner(Long userId, Long eventId) {
        return null;
    }
}
