package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public static RequestDto toRequestDto(Request request){
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
    public static List<RequestDto> toListEventShortDto(List<Request> requests){
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

}
