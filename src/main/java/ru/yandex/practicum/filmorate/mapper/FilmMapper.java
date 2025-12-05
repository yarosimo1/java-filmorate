package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(FilmCreateDto dto, Rating mpa, Set<Genre> genres) {
        Film film = new Film();
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setMpa(mpa);
        if (genres != null) {
            film.setGenres(new HashSet<>(genres));
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpaId(film.getMpa().getId());
        dto.setGenreIds(film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        dto.setWhoLikes(film.getWhoLikes());
        return dto;
    }

    public static Film updateFilmFields(Film film, FilmUpdateDto dto, Rating mpa, Set<Genre> genres) {
        if (dto.getName() != null) {
            film.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            film.setDescription(dto.getDescription());
        }
        if (dto.getReleaseDate() != null) {
            film.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getDuration() != null) {
            film.setDuration(dto.getDuration());
        }
        if (mpa != null) {
            film.setMpa(mpa);
        }
        if (genres != null) {
            film.setGenres(new HashSet<>(genres));
        }
        return film;
    }
}
