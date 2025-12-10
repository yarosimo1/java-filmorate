package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.FilmDBStorage;
import ru.yandex.practicum.filmorate.dal.FriendshipDBStorage;
import ru.yandex.practicum.filmorate.dal.UserDBStorage;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDBStorageTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmDBStorage filmStorage;

    private UserDBStorage userStorage;

    private FriendshipDBStorage friendshipDBStorage;

    @BeforeEach
    public void setUp() {
        RowMapper<Film> mapper = (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("FILM_ID"));
            film.setName(rs.getString("NAME"));
            film.setDescription(rs.getString("DESCRIPTION"));
            film.setReleaseDate(rs.getDate("RELEAS_DATE").toLocalDate());
            film.setDuration(Duration.ofMinutes(rs.getInt("DURATION")));
            // MPA и жанры можно оставить пустыми или подставить фиктивные
            return film;
        };
        filmStorage = new FilmDBStorage(jdbcTemplate, mapper);

        RowMapper<User> mapper1 = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("USER_ID"));
            user.setEmail(rs.getString("EMAIL"));
            user.setLogin(rs.getString("LOGIN"));
            user.setName(rs.getString("NAME"));

            Date birthday = rs.getDate("BIRTHDAY");
            user.setBirthday(birthday.toLocalDate());
            return user;
        };
        userStorage = new UserDBStorage(jdbcTemplate, mapper1);

        RowMapper<Friendship> mapper2 = (rs, rowNum) -> {
            return Friendship.builder()
                    .userId(rs.getLong("USER_ID"))
                    .friendId(rs.getLong("FRIEND_ID"))
                    .friendshipStatusId(rs.getLong("FRIENDSHIP_STATUS_ID"))
                    .build();
        };
        friendshipDBStorage = new FriendshipDBStorage(jdbcTemplate, mapper2);
    }

    @Test
    public void testAddFilm() {
        Film film = createSampleFilm("Inception", 148, 1L, "G");

        Film savedFilm = filmStorage.add(film);

        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo("Inception");
        assertThat(savedFilm.getDuration()).isEqualTo(Duration.ofMinutes(148));
        assertThat(savedFilm.getMpa()).isNotNull();
    }

    @Test
    public void testFindById() {
        Film film = createSampleFilm("Interstellar", 169, 2L, "PG-13");
        Film savedFilm = filmStorage.add(film);

        Optional<Film> filmOptional = filmStorage.findById(savedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f.getName()).isEqualTo("Interstellar"));
    }

    @Test
    public void testFindAll() {
        filmStorage.add(createSampleFilm("Film1", 90, 1L, "G"));
        filmStorage.add(createSampleFilm("Film2", 120, 2L, "PG-13"));

        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void testUpdateFilm() {
        Film film = createSampleFilm("Old Name", 100, 1L, "G");
        Film savedFilm = filmStorage.add(film);

        savedFilm.setName("New Name");
        savedFilm.setDescription("Updated description");
        savedFilm.setDuration(Duration.ofMinutes(110));
        Film updatedFilm = filmStorage.update(savedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("New Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");
        assertThat(updatedFilm.getDuration()).isEqualTo(Duration.ofMinutes(110));
    }

    @Test
    public void testDeleteFilm() {
        Film film = createSampleFilm("To be deleted", 110, 1L, "G");
        Film savedFilm = filmStorage.add(film);

        Film deletedFilm = filmStorage.delete(savedFilm.getId());

        assertThat(deletedFilm.getId()).isEqualTo(savedFilm.getId());

        assertThatThrownBy(() -> filmStorage.delete(savedFilm.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Фильм не найден");
    }

    @Test
    void shouldReturnCommonFilms() {
        User user = userStorage.add(createSampleUser("user", "user", "user@mail.com", LocalDate.now()));
        User user1 = userStorage.add(createSampleUser("user1", "user1", "user1@mail.com", LocalDate.now()));

        Film film = filmStorage.add(createSampleFilm("Film", 120, 1L, "PG"));
        Film film1 = filmStorage.add(createSampleFilm("Film1", 120, 2L, "G"));
        Film film2 = filmStorage.add(createSampleFilm("Film2", 120, 3L, "R"));

        friendshipDBStorage.add(Friendship.builder()
                        .userId(user.getId())
                        .friendId(user1.getId())
                        .friendshipStatusId(2L)
                .build());

        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.addLike(film1.getId(), user.getId());

        filmStorage.addLike(film.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user1.getId());

        List<Film> result = filmStorage.findCommonFilms(user.getId(), user1.getId());

        assertEquals(2, result.size());
        assertEquals(film.getId(), result.get(0).getId());
    }

    // Вспомогательный метод для создания фильма с обязательными полями
    private Film createSampleFilm(String name, long durationMinutes, Long ratingId, String ratingName) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Sample description for " + name);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(durationMinutes));
        film.setMpa(createSampleRating(ratingId, ratingName));
        film.getGenres().add(createSampleGenre(1L, "Drama")); // Добавляем хотя бы один жанр
        return film;
    }

    private Rating createSampleRating(Long ratingId, String ratingName) {
        return Rating.builder()
                .id(ratingId)
                .name(ratingName)
                .build();
    }

    private Genre createSampleGenre(Long id, String name) {
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }

    private User createSampleUser(String Login, String name, String email, LocalDate birthday) {
        User user = new User();
        user.setLogin(Login);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);

        return user;
    }
}
