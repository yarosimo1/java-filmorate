package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class UserDBStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM USERS WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " + "WHERE USER_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";

    public UserDBStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    @Override
    public User add(User user) {
        long id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        update(UPDATE_QUERY, newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday(), newUser.getId());
        return newUser;
    }

    @Override
    public User delete(Long id) {
        User user = findById(id).orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        delete(DELETE_QUERY, id);
        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        return Map.of();
    }
}
