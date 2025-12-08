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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDBStorageTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmDBStorage filmStorage;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testAddFilm() {
        Film film = createSampleFilm("Inception", 148, 1L, "G");

        Film savedFilm = filmStorage.add(film);

        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo("Inception");
        assertThat(savedFilm.getDuration()).isEqualTo(Duration.ofMinutes(148));
        assertThat(savedFilm.getMpa()).isNotNull();
    }

    @Test
    void testFindById() {
        Film film = createSampleFilm("Interstellar", 169, 2L, "PG-13");
        Film savedFilm = filmStorage.add(film);

        Optional<Film> filmOptional = filmStorage.findById(savedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f.getName()).isEqualTo("Interstellar"));
    }

    @Test
    void testFindAll() {
        filmStorage.add(createSampleFilm("Film1", 90, 1L, "G"));
        filmStorage.add(createSampleFilm("Film2", 120, 2L, "PG-13"));

        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateFilm() {
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
    void testDeleteFilm() {
        Film film = createSampleFilm("To be deleted", 110, 1L, "G");
        Film savedFilm = filmStorage.add(film);

        Film deletedFilm = filmStorage.delete(savedFilm.getId());

        assertThat(deletedFilm.getId()).isEqualTo(savedFilm.getId());

        assertThatThrownBy(() -> filmStorage.delete(savedFilm.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Фильм не найден");
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
}
