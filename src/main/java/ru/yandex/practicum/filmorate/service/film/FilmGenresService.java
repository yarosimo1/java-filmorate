package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmGenresDBStorage;
import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.util.Collection;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class FilmGenresService {
    private final FilmGenresDBStorage filmGenresDBStorage;

    public Collection<FilmGenres> getFilmGenres() {
        return filmGenresDBStorage.findAll();
    }

    public FilmGenres create(FilmGenres filmGenres) {
        FilmGenres createdFilmGenres = filmGenresDBStorage.add(filmGenres);

        return createdFilmGenres;
    }

    public void deleteGenresByFilmId(Long filmId) {
        filmGenresDBStorage.update(filmId);
    }
}