package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.dto.HitDtoRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatServerControllerTest {
    @MockBean
    StatService statService;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void postHitOk() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .app("app")
                .uri("uri")
                .ip("192.168.0.1")
                .timestamp(LocalDateTime.now().toString())
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/hit")
                .content(objectMapper.writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(statService).save(any());
    }

    @SneakyThrows
    @Test
    void postHitNullAppBadRequest() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .uri("uri")
                .ip("192.168.0.1")
                .timestamp(LocalDateTime.now().toString())
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/hit")
                .content(objectMapper.writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void postHitNullUriBadRequest() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .app("app")
                .ip("192.168.0.1")
                .timestamp(LocalDateTime.now().toString())
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/hit")
                .content(objectMapper.writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void postHitNullIpBadRequest() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .app("app")
                .uri("uri")
                .timestamp(LocalDateTime.now().toString())
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/hit")
                .content(objectMapper.writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void postHitNullTimestampBadRequest() {
        HitDtoRequest hit = HitDtoRequest.builder()
                .app("app")
                .uri("uri")
                .ip("192.168.0.1")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/hit")
                .content(objectMapper.writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllWithEmptyHit() {
        when(statService.findAll(any(), any(), anyBoolean(), anyList())).thenReturn(List.of());
        mvc.perform(MockMvcRequestBuilders.get("/stats")
                .param("start", "2023-03-13 14:00:00")
                .param("end", "2023-03-13 20:00:00")
                .param("unique", "false")
                .param("uris", "/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @SneakyThrows
    @Test
    void getAllOk() {
        ViewStats viewStats = ViewStats.builder()
                .app("new app")
                .uri("new uri")
                .hits(1L)
                .build();
        when(statService.findAll(any(), any(), anyBoolean(), anyList())).thenReturn(List.of(viewStats));
        mvc.perform(MockMvcRequestBuilders.get("/stats")
                .param("start", "2023-03-13 14:00:00")
                .param("end", "2023-03-13 20:00:00")
                .param("unique", "false")
                .param("uris", "/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].app").value(viewStats.getApp()))
                .andExpect(jsonPath("$.[0].uri").value(viewStats.getUri()))
                .andExpect(jsonPath("$.[0].hits").value(viewStats.getHits()));
    }

    @SneakyThrows
    @Test
    void getAllWithoutStart() {
        mvc.perform(MockMvcRequestBuilders.get("/stats")
                .param("end", "2023-03-13 20:00:00")
                .param("unique", "false")
                .param("uris", "/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllWithoutEnd() {
        mvc.perform(MockMvcRequestBuilders.get("/stats")
                .param("start", "2023-03-13 14:00:00")
                .param("unique", "false")
                .param("uris", "/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllWithoutUris() {
        mvc.perform(MockMvcRequestBuilders.get("/stats")
                .param("start", "2023-03-13 14:00:00")
                .param("end", "2023-03-13 20:00:00")
                .param("unique", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

