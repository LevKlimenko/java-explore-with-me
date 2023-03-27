package ru.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    String title;
    Boolean pinned;
    Set<Long> events;
}