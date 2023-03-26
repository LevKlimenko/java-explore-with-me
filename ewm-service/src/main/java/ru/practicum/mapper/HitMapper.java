package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.dto.HitRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@NoArgsConstructor
public class HitMapper {
    //private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HitRequestDto toHitRequestDto(HttpServletRequest request) {
        return HitRequestDto.builder()
                .app("emw-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
