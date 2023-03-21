package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class NewEventDto {
    @Length(min = 20, max = 2000)
    @NotNull
    String annotation;
    @NotNull
    int category;
    @NotNull
    @Length(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    Location location;
    boolean paid;
    int participantLimit;
    boolean requestModeration;
    @Length(min = 3, max = 120)
    String title;

}