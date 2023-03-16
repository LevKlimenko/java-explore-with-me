package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class StatController {
    private final StatService hitService;

    @PostMapping("/hit")
    public ResponseEntity<String> save(@RequestBody HitRequestDto hitDtoRequest) {
        hitService.save(hitDtoRequest);
        log.info("Hit have been saved, HitApp = '{}', HitIp = '{}', uri = '{}'",
                hitDtoRequest.getApp(), hitDtoRequest.getIp(), hitDtoRequest.getUri());

        return new ResponseEntity<>("Information saved", HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> findAll(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                      @RequestParam(defaultValue = "false") Boolean unique,
                                                      @RequestParam(required = false) List<String> uris) {
        List<ViewStatsDto> stats = hitService.findAll(start, end, unique, uris);
        log.info("Statistics for the period from {} to {} collected. Unique={}", start, end, unique);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}