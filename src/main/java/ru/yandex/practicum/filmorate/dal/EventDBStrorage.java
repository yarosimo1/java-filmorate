package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.*;

@Repository
public class EventDBStrorage extends BaseRepository<Event> {
    public EventDBStrorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    private final String INSERT_EVENT = "INSERT INTO EVENTS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID, EVENT_TIMESTAMP) " +
            "VALUES (?, ?, ?, ?, ?)";
    private final String SELECT_EVENT = "SELECT * FROM EVENTS WHERE USER_ID = ?";

    public List<Event> findAll(long userId) {
        return findMany(SELECT_EVENT, userId);
    }

    public Event add(Event event) {
        long id = insert(
                INSERT_EVENT,
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId(),
                event.getTimestamp()
        );

        event.setEventId(id);
        return event;
    }
}
