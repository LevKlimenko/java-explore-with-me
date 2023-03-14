package ru.practicum.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class HitMapper {

    public static HitDtoRequest toHitDtoRequest(Hit hit) {
        return new HitDtoRequest(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().toString()
        );
    }

    public static Hit toHit(HitDtoRequest hitDtoRequest) {
        Hit hit = new Hit();
        hit.setApp(hitDtoRequest.getApp());
        hit.setUri(hitDtoRequest.getUri());
        hit.setIp(hitDtoRequest.getIp());
        hit.setTimestamp(LocalDateTime.parse(hitDtoRequest.getTimestamp(),
                DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss"))));
        return hit;
    }
}