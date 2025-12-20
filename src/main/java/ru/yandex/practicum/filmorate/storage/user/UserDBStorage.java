package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDBStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM USERS WHERE email = ?";
    private static final String FIND_ALL_FRIENDSHIPS = "SELECT USER_ID, FRIEND_ID FROM FRIENDSHIP WHERE FRIENDSHIP_STATUS_ID = 2";
    private static final String FIND_USER_FRIENDSHIPS = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ? AND FRIENDSHIP_STATUS_ID = 2";
    private static final String INSERT_QUERY = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " + "WHERE USER_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";

    public UserDBStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findByEmail(String email) {
        return super.findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findById(long userId) {
        Optional<User> userOpt = super.findOne(FIND_BY_ID_QUERY, userId);

        userOpt.ifPresent(user -> {
            Set<Long> friendIds = new HashSet<>(jdbc.query(FIND_USER_FRIENDSHIPS,
                    (rs, rowNum) ->
                            rs.getLong("FRIEND_ID"), userId));
            user.setFriendships(friendIds);
        });

        return userOpt;
    }

    public List<User> findAll() {
        List<User> users = super.findMany(FIND_ALL_QUERY);

        if (users.isEmpty()) {
            return users;
        }

        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        // 2. Грузим все дружбы одним запросом
        jdbc.query(FIND_ALL_FRIENDSHIPS, rs -> {
            long userId = rs.getLong("USER_ID");
            long friendId = rs.getLong("FRIEND_ID");

            User user = userMap.get(userId);
            if (user != null) {
                user.getFriendships().add(friendId);
            }
        });

        return users;
    }

    @Override
    public User add(User user) {
        long id = super.insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        super.update(UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId());
        return newUser;
    }

    @Override
    public User delete(Long id) {
        User user = findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        super.delete(DELETE_QUERY, id);
        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        List<User> users = findAll();

        return users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }
}

