package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@Getter
@Service
public class GenreService {
    private final GenreStorage inMemoryGenreStorage;

    public GenreService(InMemoryGenreStorage inMemoryGenreStorage) {
        this.inMemoryGenreStorage = inMemoryGenreStorage;
    }

    public Collection<Genre> getGenre() {
        log.info("Получен запрос на получение всех жанров {}", inMemoryGenreStorage.getGenre().size());
        return inMemoryGenreStorage.getGenre().values();
    }

    public Genre postGenre(Genre genre) {
        log.info("Получен запрос на добавление жанра {}", genre);

        Genre createdGenre = inMemoryGenreStorage.add(genre);
        log.info("Добавлен новый фильм: id={}, name={}", createdGenre.getId(), createdGenre.getName());

        return createdGenre;
    }

    public Genre putGenre(Genre newGenre) {
        log.info("Получен запрос на обновление жанра с id={}", newGenre.getId());

        Genre oldGenre = inMemoryGenreStorage.getGenre().get(newGenre.getId());

        if (oldGenre == null) {
            log.warn("Ошибка обновления: жанра с id={} не найден", newGenre.getId());
            throw new NoSuchElementException("Жанр с id=" + newGenre.getId() + " не найден");
        }

        oldGenre.setName(newGenre.getName());

        inMemoryGenreStorage.update(oldGenre);
        log.info("Жанр id={} успешно обновлён", newGenre.getId());

        return oldGenre;
    }

    public Genre deleteGenre(Long id) {
        log.info("Получен запрос на удаление жанра с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: жанра с id={} не найден", id);
            throw new NoSuchElementException("Жанр с id=" + id + " не найден");
        }

        Genre deletedGenre = inMemoryGenreStorage.delete(id);

        log.info("Жанр с id={} успешно удален", deletedGenre.getId());

        return deletedGenre;
    }
}
