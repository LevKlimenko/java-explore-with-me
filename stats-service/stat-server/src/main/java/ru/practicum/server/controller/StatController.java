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
import ru.practicum.server.mapper.StatMapper;
import ru.practicum.server.service.StatService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class StatController {
    private final StatService hitService;

    @PostMapping("/hit")
    public ResponseEntity<Object> save(@RequestBody HitRequestDto hitDtoRequest, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        hitService.save(StatMapper.toHit(hitDtoRequest), StatMapper.toApp(hitDtoRequest.getApp()), ip);
        log.info("Hit have been saved, HitApp = '{}', uri = '{}' from IP={}",
                hitDtoRequest.getApp(), hitDtoRequest.getUri(), ip);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> findAll(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(defaultValue = "false") Boolean unique,
                                          @RequestParam(required = false) List<String> uris) {
        List<ViewStatsDto> stats = hitService.findAll(start, end, unique, uris);
        log.info("Statistics for the period from {} to {} collected. Unique={}", start, end, unique);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}