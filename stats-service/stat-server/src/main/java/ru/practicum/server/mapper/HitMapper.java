package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.Hit;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class HitMapper {

    public static HitRequestDto toHitDtoRequest(Hit hit) {
        return new HitRequestDto(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().toString()
        );
    }

    public static Hit toHit(HitRequestDto hitDtoRequest) {
        Hit hit = new Hit();
        hit.setApp(hitDtoRequest.getApp());
        hit.setUri(hitDtoRequest.getUri());
        hit.setIp(hitDtoRequest.getIp());
        hit.setTimestamp(LocalDateTime.parse(hitDtoRequest.getTimestamp(),
                DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss"))));
        return hit;
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(
                viewStats.getApp(),
                viewStats.getUri(),
                viewStats.getHits()
        );
    }
}