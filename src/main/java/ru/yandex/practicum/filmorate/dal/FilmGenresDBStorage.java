package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenresDBStorage extends BaseRepository<FilmGenres> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILMS_GENRES";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String FIND_BY_GENRE_ID_QUERY = "SELECT * FROM FILMS_GENRES WHERE GENRE_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILMS_GENRES SET GENRE_ID = ? WHERE FILM_ID = ? AND GENRE_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";


    public FilmGenresDBStorage(JdbcTemplate jdbc, RowMapper<FilmGenres> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenres> findAllByFilmId(long filmId) {
        List<FilmGenres> filmGenres = findMany(FIND_BY_FILM_ID_QUERY, filmId);
        return filmGenres;
    }

    public Optional<FilmGenres> findByGenreId(long genreId) {
        Optional<FilmGenres> filmGenres = findOne(FIND_BY_GENRE_ID_QUERY, genreId);
        return filmGenres;
    }

    public List<FilmGenres> findAll() {
        List<FilmGenres> filmGenres = findMany(FIND_ALL_QUERY);
        return filmGenres;
    }

    public FilmGenres add(FilmGenres filmGenres) {
        update(INSERT_QUERY, filmGenres.getFilmId(),  // сначала ID фильма
                filmGenres.getGenreId()  // потом ID жанра
        );
        return filmGenres;
    }

    public void update(Long filmId) {
        update(DELETE_QUERY, filmId);
    }

    public void delete(Long genreId) {
        delete(DELETE_QUERY, genreId);
    }
}
