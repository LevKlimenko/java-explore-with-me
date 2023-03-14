package ru.practicum.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class HitDtoRequestTest {
    @Autowired
    private JacksonTester<HitDtoRequest> json;

    @SneakyThrows
    @Test
    void testHitDtoRequest() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .app("app")
                .uri("uri")
                .ip("192.168.0.1")
                .timestamp(LocalDateTime.now().toString())
                .build();

        JsonContent<HitDtoRequest> result = json.write(hit);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo(hit.getApp());
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo(hit.getUri());
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo(hit.getIp());
        assertThat(result).extractingJsonPathStringValue("$.timestamp").isEqualTo(hit.getTimestamp());
    }
}
