package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        filmStorage.put(++index, film);

        return Optional.of(film);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.values()
                .stream()
                .toList();
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
