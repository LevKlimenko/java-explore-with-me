package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.Hit;
import ru.practicum.server.model.ViewStats;

@UtilityClass
public class HitMapper {
    public static HitRequestDto toHitDtoRequest(Hit hit) {
        return new HitRequestDto(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }

    public static Hit toHit(HitRequestDto hitDtoRequest) {
        Hit hit = new Hit();
        hit.setApp(hitDtoRequest.getApp());
        hit.setUri(hitDtoRequest.getUri());
        hit.setIp(hitDtoRequest.getIp());
        hit.setTimestamp(hitDtoRequest.getTimestamp());
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