package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.comment.dto.CommentFullDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDtoForAdmin extends EventFullDto {
    List<CommentFullDto> comments;
}
