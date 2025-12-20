package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipDBStorage extends BaseRepository<Friendship> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FRIENDSHIP";
    private static final String FIND_BY_USER_AND_FRIEND = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FRIENDSHIP " + "(USER_ID, FRIEND_ID, FRIENDSHIP_STATUS_ID) " + "VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FRIENDSHIP SET " + "FRIENDSHIP_STATUS_ID = ? " + "WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FRIENDSHIP WHERE FRIEND_ID = ?";

    public FriendshipDBStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public List<Friendship> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Friendship> findById(Long userId, Long friendId) {
        return findOne(FIND_BY_USER_AND_FRIEND, userId, friendId);
    }

    public Friendship add(Friendship friendship) {
        // Теперь не возвращаем ID, так как его нет
        update(INSERT_QUERY, friendship.getUserId(), friendship.getFriendId(), friendship.getFriendshipStatusId());
        return friendship;
    }

    public Friendship update(Friendship friendship) {
        update(UPDATE_QUERY, friendship.getFriendshipStatusId(), friendship.getUserId(), friendship.getFriendId());
        return friendship;
    }

    public void delete(Long friendId) {
        delete(DELETE_QUERY, friendId);
    }
}
