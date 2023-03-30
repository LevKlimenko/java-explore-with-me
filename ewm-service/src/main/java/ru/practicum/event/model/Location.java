package ru.practicum.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    Float lat;
    Float lon;
}