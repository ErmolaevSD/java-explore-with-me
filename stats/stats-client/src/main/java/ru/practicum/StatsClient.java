package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatsClient {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestClient restClient;

    @Value("${stat-svc-service.url}")
    private  String statServiceUrl;

    public StatsClient(@Value("${stat-svc-service.url}") String statServiceUrl) {
        restClient = RestClient.builder()
                .baseUrl(statServiceUrl)
                .build();
    }

    public ResponseEntity<Object> saveHit(EndpointHit hit) {
        try {
            log.info("Сохранение информации о запросе {}", hit);
            return ResponseEntity.status(HttpStatus.CREATED).body(restClient.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(hit)
                    .retrieve()
                    .body(EndpointHit.class));
        } catch (Exception e) {
            log.error("Oшибка при сохранении информации");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public List<ViewStatDto> getStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      boolean unique) {
        try {
            log.info("Запрос статистики {}", start);


            ResponseEntity<List<ViewStatDto>> response = restClient.get()
                    .uri(uriBuilder -> {
                        UriBuilder builder = uriBuilder.path("/stats")
                                .queryParam("start", start.format(FORMATTER))
                                .queryParam("end", end.format(FORMATTER))
                                .queryParam("unique", unique);
                        if (uris != null && !uris.isEmpty()) {
                            for (String uri : uris) {
                                builder.queryParam("uris", uri);
                            }
                        }
                        return builder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<ViewStatDto>>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK) {

                return response.getBody();

            } else {
                log.error("Ошибка при получении статистики: {}", response.getStatusCode());
                throw new RuntimeException();
            }

        } catch (Exception e) {
            log.error("Ошибка при запросе статистики: {}", e.getMessage());
            throw new RuntimeException();
        }
    }
}