package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDBStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Getter
@Service
@AllArgsConstructor
public class GenreService {
    @Qualifier("InMemoryGenreStorage")
    private final GenreStorage inMemoryGenreStorage;
    @Qualifier("GenreDBStorage")
    private final GenreDBStorage genreDBStorage;

    public Collection<Genre> getGenres() {
        log.info("Получен запрос на получение всех жанров");
        return genreDBStorage.findAll();
    }

    public Genre getGenreById(Long id) {
        log.info("Получен запрос на получение жанра");
        return genreDBStorage.findById(id).get();
    }

    public Genre postGenre(Genre genre) {
        log.info("Получен запрос на добавление жанра {}", genre);

        Optional<Genre> alreadyExistUser = genreDBStorage.findByName(genre.getName());

        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный жанр уже существует");
        }

        Genre createdGenre = genreDBStorage.add(genre);
        log.info("Добавлен новый фильм: id={}, name={}", createdGenre.getName());

        return createdGenre;
    }

    public Genre putGenre(Genre newGenre) {
        log.info("Получен запрос на обновление жанра с id={}", newGenre.getId());

        Genre oldGenre = genreDBStorage.findById(newGenre.getId()).orElseThrow(() -> new NoSuchElementException("Жанр не найден"));

        oldGenre.setName(newGenre.getName());

        genreDBStorage.update(oldGenre);
        log.info("Жанр id={} успешно обновлён", newGenre.getId());

        return oldGenre;
    }

    public Genre deleteGenre(Long id) {
        log.info("Получен запрос на удаление жанра с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: жанра с id={} не найден", id);
            throw new NoSuchElementException("Жанр с id=" + id + " не найден");
        }

        Genre deletedGenre = genreDBStorage.delete(id);

        log.info("Жанр с id={} успешно удален", deletedGenre.getId());

        return deletedGenre;
    }
}
