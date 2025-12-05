package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("FILM_ID"));
        film.setName(rs.getString("NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setDuration(Duration.ofMinutes(rs.getInt("DURATION")));

        Date releaseDate = rs.getDate("RELEAS_DATE");
        film.setReleaseDate(releaseDate.toLocalDate());

        Rating mpa = new Rating();
        mpa.setId(rs.getLong("RATING_MPA_ID"));
        film.setMpa(mpa);

        return film;
    }
}