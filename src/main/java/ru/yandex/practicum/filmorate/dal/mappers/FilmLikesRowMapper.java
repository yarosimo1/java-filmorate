package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmLikesRowMapper implements RowMapper<FilmLikes> {
    @Override
    public FilmLikes mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmLikes filmLikes = new FilmLikes();
        filmLikes.setFilmId(rs.getLong("FILM_ID"));
        filmLikes.setUserId(rs.getLong("USER_ID"));
        return filmLikes;
    }
}
