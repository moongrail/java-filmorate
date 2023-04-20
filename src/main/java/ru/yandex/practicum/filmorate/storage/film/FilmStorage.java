package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> save(Film film);

    List<Film> getAll();

    Optional<Film> update(Film film);

    Optional<Film> updateById(Long id, Film film);

    void delete(Long id);

    Optional<Film> getById(Long id);
}
