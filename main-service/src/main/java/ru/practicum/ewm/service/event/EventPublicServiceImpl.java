package ru.practicum.ewm.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventPublicFilter;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.entity.event.Event;
import ru.practicum.ewm.entity.event.EventSort;
import ru.practicum.ewm.entity.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final StatsClient statClient;
    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getAll(EventPublicFilter publicFilter, Integer from, Integer size,
                                      HttpServletRequest httpServletRequest) {
        publicFilter.validateDates();
        Specification<Event> specification = DbSpecification.getPublicSpecification(
                publicFilter.getText(),
                publicFilter.getCategoryIds(),
                publicFilter.getPaid(),
                publicFilter.getRangeStart(),
                publicFilter.getRangeEnd(),
                publicFilter.getOnlyAvailable());
        Sort sort = Optional.ofNullable(publicFilter.getSort())
                .map(s -> Sort.by(Sort.Direction.DESC, s == EventSort.EVENT_DATE ? "eventDate" : "views"))
                .orElse(Sort.unsorted());
        hit(httpServletRequest);
        return eventRepository.findAll(specification, PageRequest.of(from / size, size).withSort(sort))
                .stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = getById(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("getById: Событие id = %d не опубликовано".formatted(eventId));
        }
        List<ViewStatDto> stats = statClient.getStats(event.getPublishedOn(), LocalDateTime.now(), List.of("/events/" + eventId), true);

        log.info("Метод getById, длина списка stats: {}", stats.size());
        Long views = stats.isEmpty() ? 0L : stats.getFirst().getGetHits();
        event.setViews(views);
        log.info("Метод getById, количество сохраняемых просмотров: {}", views);
        hit(httpServletRequest);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public Optional<Event> findById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public Event getById(Long eventId) {
        return findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие id = %d не найдено".formatted(eventId)));
    }

    private void hit(HttpServletRequest httpServletRequest) {
        EndpointHit hitDtoRequest = new EndpointHit(
                null,
                "main-server",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now()
        );
        statClient.saveHit(hitDtoRequest);
    }
}
