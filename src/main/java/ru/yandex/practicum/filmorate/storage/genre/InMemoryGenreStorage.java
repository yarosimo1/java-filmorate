package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryGenreStorage implements GenreStorage {
    Map<Long, Genre> genres = new HashMap<>();

    @Override
    public Genre add(Genre genre) {
        log.info("Добавление жанра Genre={}", genre);
        genre.setId(getNextId());
        genres.put(genre.getId(), genre);
        return genre;
    }

    @Override
    public Genre update(Genre newGenre) {
        log.info("Обновление жанра Genre={}, на \n newGenre={}",
                genres.get(newGenre.getId()), newGenre);
        return genres.put(newGenre.getId(), newGenre);
    }

    @Override
    public Genre delete(Long id) {
        log.info("Удаление фильма Genre={}", genres.get(id));
        return genres.remove(id);
    }

    @Override
    public Map<Long, Genre> getGenre() {
        return genres;
    }

    private long getNextId() {
        long currentMaxId = genres.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
