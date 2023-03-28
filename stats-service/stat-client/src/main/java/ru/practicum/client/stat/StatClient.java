package ru.practicum.client.stat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.client.BaseClient;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatClient extends BaseClient {
    @Value("${app.name}")
    String app;

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> saveHit(HttpServletRequest request) {
        HitRequestDto hitRequestDto = new HitRequestDto();
        hitRequestDto.setApp(app);
        hitRequestDto.setUri(request.getRequestURI());
        hitRequestDto.setTimestamp(LocalDateTime.now());
        hitRequestDto.setIp(request.getRemoteAddr());
        return post("/hit", hitRequestDto);
    }

    public ResponseEntity<Object> getStat(String start, String end, Boolean unique, String[] uris) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("unique", unique);
        if (uris != null && uris.length != 0) {
            parameters.put("uris", uris);
        }
        if (parameters.containsKey("uris")) {
            return get("/stats?start={}&end={}&unique={}&uris={}", null, parameters);
        } else {
            return get("/stats?start={}&end={}&unique={}", null, parameters);
        }
    }

    public List<ViewStatsDto> getViews(Set<Long> eventsId, String start, String end) {
        List<String> uris = new ArrayList<>();
        eventsId.forEach(e -> uris.add("/events/" + e));
        Map<String, Object> parameters = Map.of(
                "uris", uris,
                "start", start,
                "end", end);
        ResponseEntity<List<ViewStatsDto>> responseEntity = rest
                .exchange("/stats?start={start}&end={end}&uris={uris}", HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {
                        }, parameters);
        return responseEntity.getBody();
    }
}