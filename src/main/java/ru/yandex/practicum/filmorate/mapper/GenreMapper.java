package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.genre.GenreCreateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreUpdateDto;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {

    public static Genre mapToGenre(GenreCreateDto dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        return genre;
    }

    public static GenreDto mapToDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre updateGenreFields(Genre genre, GenreUpdateDto dto) {
        if (dto.getName() != null) genre.setName(dto.getName());
        return genre;
    }
}
