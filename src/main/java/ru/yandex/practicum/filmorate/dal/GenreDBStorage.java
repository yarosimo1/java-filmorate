package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class GenreDBStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    private static final String FIND_BY_GENRE_NAME_QUERY = "SELECT * FROM GENRE WHERE NAME = ?";
    private static final String INSERT_QUERY = "INSERT INTO GENRE (NAME) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE GENRE SET NAME = ? WHERE GENRE_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM GENRE WHERE GENRE_ID = ?";

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Genre> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findByName(String name) {
        return findOne(FIND_BY_GENRE_NAME_QUERY, name);
    }

    @Override
    public Genre add(Genre genre) {
        long id = insert(INSERT_QUERY, genre.getName());
        genre.setId(id);
        return genre;
    }

    @Override
    public Genre update(Genre newGenre) {
        update(UPDATE_QUERY, newGenre.getName(), newGenre.getId());
        return newGenre;
    }

    @Override
    public Genre delete(Long id) {
        Genre genre = findById(id).orElseThrow(() -> new NoSuchElementException("Жанр не найден"));
        delete(DELETE_QUERY, id);
        return genre;
    }

    @Override
    public Map<Long, Genre> getGenre() {
        return Map.of();
    }
}
