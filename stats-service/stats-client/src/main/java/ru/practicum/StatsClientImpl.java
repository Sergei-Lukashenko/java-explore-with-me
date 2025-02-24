package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {
    @Value("${stats-server.url}")
    private String baseUri;

    private final RestClient restClient;

    @Override
    public StatsHitDto hit(StatsHitDto statsHitDto) {
        log.info("Сохраняю статистику для {}", statsHitDto);
        try {
            return restClient.post()
                    .uri(baseUri + "/hit")
                    .body(statsHitDto)
                    .retrieve()
                    .body(StatsHitDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<StatsViewDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("Запрос статистики с параметрами: start = {}, end = {}, uris = {}, unique ={}",
                start, end, uris, unique);
        try {
            UriComponents uriComponents = UriComponentsBuilder
                    .fromUriString(baseUri + "/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParamIfPresent("uris", Optional.ofNullable(uris))
                    .queryParam("unique", unique)
                    .encode()
                    .build();
            log.debug("uriComponents encoded {}", uriComponents.toUri());
            return restClient.get()
                    .uri(uriComponents.toUri())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatsViewDto>>() {
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }
}
