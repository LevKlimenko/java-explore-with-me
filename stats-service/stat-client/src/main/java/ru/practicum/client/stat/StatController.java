package ru.practicum.client.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping
public class StatController {
    private final StatClient statClient;

    @PostMapping(path = "/hit")
    public ResponseEntity<Object> save(@RequestBody @Valid HttpServletRequest request) {
        log.info("Сохранение данных");
        statClient.saveHit(request);
        return new ResponseEntity<>("Information saved", HttpStatus.CREATED);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<Object> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(defaultValue = "false") Boolean unique,
                                          @RequestParam(required = false) List<String> uris) {
        log.info("Получение данных");
        return statClient.getStat(start.toString(), end.toString(), unique, uris.toArray(String[]::new));
    }
}