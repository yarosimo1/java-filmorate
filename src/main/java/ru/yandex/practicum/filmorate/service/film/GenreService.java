package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.GenreDBStorage;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.mapper.model.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDBStorage genreDBStorage;

    public Collection<GenreDto> getGenres() {
        log.info("Получен запрос на получение всех жанров");
        return genreDBStorage.findAll()
                .stream()
                .map(GenreMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(Long id) {
        log.info("Получен запрос на получение жанра id={}", id);

        Genre genre = genreDBStorage.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Жанр не найден"));

        return GenreMapper.mapToDto(genre);
    }
}
