package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.List;

public class FriendshipStatusDBStorage extends BaseRepository<FriendshipStatus> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FRIENDSHIP_STATUS";

    public FriendshipStatusDBStorage(JdbcTemplate jdbc, RowMapper<FriendshipStatus> mapper) {
        super(jdbc, mapper);
    }

    public List<FriendshipStatus> findAll() {
        List<FriendshipStatus> friendshipStatuses = findMany(FIND_ALL_QUERY);
        return friendshipStatuses;
    }
}
