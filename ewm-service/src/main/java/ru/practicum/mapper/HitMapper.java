package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.dto.HitRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@NoArgsConstructor
public class HitMapper {
    public static HitRequestDto toHitRequestDto(HttpServletRequest request) {
        return HitRequestDto.builder()
                .app("emw-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}