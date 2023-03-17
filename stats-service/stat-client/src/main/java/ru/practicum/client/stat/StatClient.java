package ru.practicum.client.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.client.BaseClient;
import ru.practicum.dto.HitRequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> saveHit(HitRequestDto hitRequestDto) {
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
}