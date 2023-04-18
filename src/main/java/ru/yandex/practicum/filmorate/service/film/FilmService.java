package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film add(Film film);

    Film update(Film film);

    Film updateById(Long id, Film film);

    void deleteById(Long id);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    List<Film> getPopularFilms(short count);

    List<Film> getAll();

    Film getById(Long id);

    Film getFilmFull(Long id);
}