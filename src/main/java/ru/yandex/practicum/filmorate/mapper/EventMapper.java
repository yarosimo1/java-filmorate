package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.event.EventCreteDto;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.model.Event;


public final class EventMapper {
    public static Event mapToEvent(EventCreteDto dto) {
        return Event.builder()
                .timestamp(dto.getTimestamp())
                .entityId(dto.getEntityId())
                .eventType(dto.getEventType())
                .operation(dto.getOperation())
                .userId(dto.getUserId())
                .build();
    }

    public static EventDto mapToEventDto(Event film) {
        return EventDto.builder()
                .eventId(film.getEventId())
                .entityId(film.getEntityId())
                .eventType(film.getEventType())
                .operation(film.getOperation())
                .timestamp(film.getTimestamp())
                .userId(film.getUserId())
                .build();
    }
}
