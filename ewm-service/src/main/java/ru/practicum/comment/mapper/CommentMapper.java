package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .createdOn(comment.getCreatedOn())
                .lastUpdate(comment.getLastUpdate())
                .authorName(comment.getUser().getName())
                .text(comment.getText())
                .status(comment.getStatus())
                .build();
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .createdOn(comment.getCreatedOn())
                .lastUpdate(comment.getLastUpdate())
                .authorName(comment.getUser().getName())
                .text(comment.getText())
                .build();
    }

    public static List<CommentShortDto> toCommentShortDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
    }

    public static List<CommentFullDto> toCommentFullDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }
}