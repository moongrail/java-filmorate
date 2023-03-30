package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private static Long index = 0L;
    private final Map<Long, Film> filmStorage = new HashMap<>();

    @Override
    public void delete(Long id) {
        if (filmStorage.containsKey(id)) {
            filmStorage.remove(id);
        }
    }

    @Override
    public Optional<Film> save(Film film) {
        if (filmStorage.containsKey(film.getId())) {
            return Optional.empty();
        }

        film.setId(getId());
        filmStorage.put(film.getId(), film);

        return Optional.of(film);
    }

    private static Long getId() {
        return ++index;
    }

    @Override
    public Optional<Film> getById(Long id) {
        if (filmStorage.containsKey(id)) {
            return Optional.of(filmStorage.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = filmStorage.values()
                .stream()
                .collect(Collectors.toList());

        return films;
    }

    @Override
    public Optional<Film> update(Film film) {

        if (!filmStorage.containsKey(film.getId())) {
            return Optional.empty();
        }

        filmStorage.put(film.getId(), film);

        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateById(Long id, Film film) {
        if (!filmStorage.containsKey(id)) {
            return Optional.empty();
        }

        film.setId(id);
        filmStorage.put(id, film);

        return Optional.of(film);
    }
}
