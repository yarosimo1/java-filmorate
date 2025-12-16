package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDBStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final FilmDBStorage filmDBStorage;

    public List<Film> getFilmsByQuery(String query, String by) {
        validateParameterString(by);
        String[] parameters = by.split(",");
        if (parameters.length == 1) {
            switch (parameters[0]) {
                case "director":
                    return getFilmsByDirector(query);
                case "title":
                    return getFilmsByTitle(query);
            }
        }
        return getFilmsByCombined(query);
    }

    public List<Film> getFilmsByTitle(String query) {
        return filmDBStorage.findAll().stream()
                .filter(f -> f.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Film> getFilmsByDirector(String query) {
        return filmDBStorage.findAll().stream()
                .filter(f -> f.getDirectors().stream()
                        .anyMatch(d -> d.getName().toLowerCase().contains(query.toLowerCase())))
                .toList();
    }

    public List<Film> getFilmsByCombined(String query) {
        List<Film> filmsByTitle = getFilmsByTitle(query);
        List<Film> filmsByDirector = getFilmsByDirector(query);

        return Stream.concat(filmsByTitle.stream(), filmsByDirector.stream())
                .distinct()
                .toList();
    }

    private void validateParameterString(String by) {
        String[] parameters = by.split(",");
        if (parameters.length < 1 || parameters.length > 2)
            throw new ConditionsNotMetException("В поиск передано неверное количество параметров");
        for (String parameter : parameters) {
            if (!isValidParameter(parameter))
                throw new ConditionsNotMetException("В поиск передан недопустимый параметр");
        }
    }

    private boolean isValidParameter(String parameter) {
        return parameter.equals("director") || parameter.equals("title");
    }
}
