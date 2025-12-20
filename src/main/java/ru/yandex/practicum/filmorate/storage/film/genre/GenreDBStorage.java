package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDBStorage extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    private static final String FIND_BY_GENRE_NAME_QUERY = "SELECT * FROM GENRE WHERE NAME = ?";

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Genre> findById(long genreId) {
        return super.findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> findAll() {
        return super.findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findByName(String name) {
        return super.findOne(FIND_BY_GENRE_NAME_QUERY, name);
    }
}
