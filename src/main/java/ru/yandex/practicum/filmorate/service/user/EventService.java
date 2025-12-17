package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventDBStorage;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {
    private EventDBStorage eventDBStorage;

    public List<EventDto> getEventsByIdUser(long userId) {
        List<Event> events = eventDBStorage.findAll(userId);

        if (events.isEmpty())
            throw new NotFoundException("Фид пользователя не найден");

        return events.stream()
                .map(EventMapper::mapToEventDto)
                .toList();
    }

    public EventDto addEventToUser(Event event) {
        return EventMapper.mapToEventDto(eventDBStorage.add(event));
    }
}
