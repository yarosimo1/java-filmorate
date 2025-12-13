package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(FilmCreateDto dto) {
        Film film = new Film();
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setMpa(RatingMapper.mapToRating(dto.getMpa()));
        if (dto.getGenres() != null) {
            film.setGenres(dto.getGenres().stream()
                    .sorted(Comparator.comparing(GenreDto::getId))
                    .map(GenreMapper::mapToGenre).collect(Collectors.toSet()));
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
        dto.setMpa(RatingMapper.mapToDto(film.getMpa()));

        dto.setGenres(film.getGenres().stream()
                .map(GenreMapper::mapToDto)
                .sorted(Comparator.comparingLong(GenreDto::getId))
                .toList());


        dto.setLikes(film.getWhoLikes());
        dto.setDirectors(film.getDirectors());

        return dto;
    }

    public static Film updateFilmFields(Film film, FilmUpdateDto dto) {
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
        if (dto.getMpa().getId() != null) {
            film.setMpa(RatingMapper.mapToRating(dto.getMpa()));
        }
        if (dto.getGenres() != null) {
            film.setGenres(
                    new HashSet<>(dto.getGenres()
                            .stream()
                    .map(GenreMapper::mapToGenre)
                    .collect(Collectors.toSet()))
            );
        }
        return film;
    }
}
