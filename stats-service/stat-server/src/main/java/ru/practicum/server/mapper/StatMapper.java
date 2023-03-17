package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.App;
import ru.practicum.server.model.Hit;
import ru.practicum.server.model.ViewStats;

@UtilityClass
public class StatMapper {
    public static HitRequestDto toHitDtoRequest(Hit hit) {
        return new HitRequestDto(
                hit.getApp().toString(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }

    public static Hit toHit(HitRequestDto hitDtoRequest) {
        return Hit.builder()
                .ip(hitDtoRequest.getIp())
                .uri(hitDtoRequest.getUri())
                .timestamp(hitDtoRequest.getTimestamp())
                .build();
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(
                viewStats.getApp(),
                viewStats.getUri(),
                viewStats.getHits()
        );
    }

    public static App toApp(String appName) {
        return App.builder()
                .name(appName)
                .build();
    }
}