package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDBStorage;
import ru.yandex.practicum.filmorate.dto.genre.GenreCreateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreUpdateDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    @Qualifier("InMemoryGenreStorage")
    private final GenreStorage inMemoryGenreStorage;

    @Qualifier("GenreDBStorage")
    private final GenreDBStorage genreDBStorage;

    public Collection<GenreDto> getGenres() {
        log.info("Получен запрос на получение всех жанров");
        return genreDBStorage.findAll().stream().map(GenreMapper::mapToDto).collect(Collectors.toList());
    }

    public GenreDto getGenreById(Long id) {
        log.info("Получен запрос на получение жанра id={}", id);

        Genre genre = genreDBStorage.findById(id).orElseThrow(() -> new NoSuchElementException("Жанр не найден"));

        return GenreMapper.mapToDto(genre);
    }

    public GenreDto postGenre(GenreCreateDto dto) {
        log.info("Получен запрос на добавление жанра: {}", dto);

        Optional<Genre> existed = genreDBStorage.findByName(dto.getName());
        if (existed.isPresent()) {
            throw new DuplicatedDataException("Данный жанр уже существует");
        }

        Genre genre = GenreMapper.mapToGenre(dto);
        Genre created = genreDBStorage.add(genre);

        log.info("Добавлен новый жанр: id={} name={}", created.getId(), created.getName());

        return GenreMapper.mapToDto(created);
    }

    public GenreDto putGenre(GenreUpdateDto dto) {
        log.info("Получен запрос на обновление жанра id={}", dto.getId());

        Genre existing = genreDBStorage.findById(dto.getId()).orElseThrow(() -> new NoSuchElementException("Жанр не найден"));

        GenreMapper.updateGenreFields(existing, dto);

        genreDBStorage.update(existing);

        log.info("Жанр id={} успешно обновлён", dto.getId());

        return GenreMapper.mapToDto(existing);
    }

    public GenreDto deleteGenre(Long id) {
        log.info("Получен запрос на удаление жанра id={}", id);

        if (id == null || id <= 0) {
            throw new NoSuchElementException("Некорректный id: " + id);
        }

        Genre deleted = genreDBStorage.delete(id);

        log.info("Жанр id={} успешно удалён", id);

        return GenreMapper.mapToDto(deleted);
    }
}
