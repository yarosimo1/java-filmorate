package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.UserDBStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
class UserDBStorageTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDBStorage userStorage;

    @BeforeEach
    void setUp() {
        RowMapper<User> mapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("USER_ID"));
            user.setEmail(rs.getString("EMAIL"));
            user.setLogin(rs.getString("LOGIN"));
            user.setName(rs.getString("NAME"));
            user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
            return user;
        };
        userStorage = new UserDBStorage(jdbcTemplate, mapper);
    }

    @Test
    void testAddUser() {
        User user = createSampleUser("user@example.com", "user1");

        User savedUser = userStorage.add(user);

        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getEmail()).isEqualTo("user@example.com");
        assertThat(savedUser.getLogin()).isEqualTo("user1");
    }

    @Test
    void testFindById() {
        User user = createSampleUser("findbyid@example.com", "finduser");
        User savedUser = userStorage.add(user);

        Optional<User> found = userStorage.findById(savedUser.getId());

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getLogin()).isEqualTo("finduser"));
    }

    @Test
    void testFindAll() {
        userStorage.add(createSampleUser("all1@example.com", "all1"));
        userStorage.add(createSampleUser("all2@example.com", "all2"));

        List<User> users = userStorage.findAll();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUser() {
        User user = createSampleUser("old@example.com", "oldlogin");
        User savedUser = userStorage.add(user);

        savedUser.setLogin("newlogin");
        savedUser.setEmail("new@example.com");
        User updatedUser = userStorage.update(savedUser);

        assertThat(updatedUser.getLogin()).isEqualTo("newlogin");
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testDeleteUser() {
        User user = createSampleUser("delete@example.com", "tobedeleted");
        User savedUser = userStorage.add(user);

        User deleted = userStorage.delete(savedUser.getId());

        assertThat(deleted.getId()).isEqualTo(savedUser.getId());

        assertThatThrownBy(() -> userStorage.delete(savedUser.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void testFindByEmail() {
        User user = createSampleUser("emailtest@example.com", "emailuser");
        User savedUser = userStorage.add(user);

        Optional<User> found = userStorage.findByEmail("emailtest@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedUser.getId());
    }

    private User createSampleUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}
