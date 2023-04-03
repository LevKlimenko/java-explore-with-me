package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.event.dto.EventFullDtoForAdmin;
import ru.practicum.event.dto.EventFullDtoForUser;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public static EventFullDtoForUser toEventFullDtoForUser(Event event) {
        EventFullDtoForUser e = new EventFullDtoForUser();
        e.setId(event.getId());
        e.setAnnotation(event.getAnnotation());
        e.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        e.setConfirmedRequests(event.getConfirmedRequests());
        e.setCreatedOn(event.getCreatedOn());
        e.setDescription(event.getDescription());
        e.setLocation(event.getLocation());
        e.setEventDate(event.getEventDate());
        e.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        e.setPaid(event.getPaid());
        e.setParticipantLimit(event.getParticipantLimit());
        e.setPublishedOn(event.getPublishedOn());
        e.setRequestModeration(event.getRequestModeration());
        e.setState(event.getState());
        e.setTitle(event.getTitle());
        e.setViews(event.getViews());
        e.setCommentingClosed(event.getCommentingClosed());
        e.setCommentModeration(event.getCommentModeration());
        e.setComments(CommentMapper.toCommentShortDtoList(
                event.getComments() == null ? new ArrayList<>() : event.getComments()));
        return e;
    }

    public static EventFullDtoForAdmin toEventFullDtoForAdmin(Event event) {
        EventFullDtoForAdmin e = new EventFullDtoForAdmin();
        e.setId(event.getId());
        e.setAnnotation(event.getAnnotation());
        e.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        e.setConfirmedRequests(event.getConfirmedRequests());
        e.setCreatedOn(event.getCreatedOn());
        e.setDescription(event.getDescription());
        e.setLocation(event.getLocation());
        e.setEventDate(event.getEventDate());
        e.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        e.setPaid(event.getPaid());
        e.setParticipantLimit(event.getParticipantLimit());
        e.setPublishedOn(event.getPublishedOn());
        e.setRequestModeration(event.getRequestModeration());
        e.setState(event.getState());
        e.setTitle(event.getTitle());
        e.setViews(event.getViews());
        e.setCommentingClosed(event.getCommentingClosed());
        e.setCommentModeration(event.getCommentModeration());
        e.setComments(CommentMapper.toCommentFullDtoList(
                event.getComments() == null ? new ArrayList<>() : event.getComments()));
        return e;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventShortDto> toListEventShortDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

}