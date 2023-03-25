package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    Optional<Film> add(Film film);

    Optional<Film> update(Film film);

    Optional<Film> updateById(Long id, Film film);

    void deleteById(Long id);

    void addLike(Long userId);

    void removeLike(Long userId);

    List<Film> getPopularFilms(short count);
}
