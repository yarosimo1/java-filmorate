package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenresRowMapper implements RowMapper<FilmGenres> {

    @Override
    public FilmGenres mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenres filmGenres = FilmGenres.builder().filmId(rs.getLong("FILM_ID")).genreId(rs.getLong("GENRE_ID")).build();
        return filmGenres;
    }
}
