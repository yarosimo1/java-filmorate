package ru.yandex.practicum.filmorate.mapper.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Friendship.builder()
                .userId(rs.getLong("USER_ID"))
                .friendId(rs.getLong("FRIEND_ID"))
                .friendshipStatusId(rs.getLong("FRIENDSHIP_STATUS_ID"))
                .build();
    }
}
