package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    @Transactional
    public EndpointHit saveHit(EndpointHit hitDto) {
        log.info("Saving hit: {}", hitDto);
        Hit hit = hitMapper.toEntity(hitDto);
        hit.setCreatedAt(hitDto.getTimestamp());
        return hitMapper.toDto(hitRepository.save(hit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        log.info("Getting stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        List<ViewStats> rows;

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                rows = hitRepository.getStatsUniqueIpWithoutUris(start, end);
            } else {
                rows = hitRepository.getStatsWithoutUris(start, end);
            }
        } else {
            if (unique) {
                rows = hitRepository.getStatsUniqueIp(start, end, uris);
            } else {
                rows = hitRepository.getStats(start, end, uris);
            }
        }
        return rows;
    }
}
