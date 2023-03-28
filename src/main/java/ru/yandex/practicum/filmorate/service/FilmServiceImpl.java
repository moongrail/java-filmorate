package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    @Override
    public Film add(Film film) {
        Optional<Film> save = filmStorage.save(film);

        if (save.isEmpty()) {
            throw new IncorrectParameterException("Фильм с таким айди уже существует.");
        }

        return save.get();
    }

    @Override
    public Film update(Film film) {
        Optional<Film> update = filmStorage.update(film);

        if (update.isEmpty()) {
            throw new FilmNotFoundException("Несуществующий идентификатор фильма");
        }

        return update.get();
    }

    @Override
    public Film updateById(Long id, Film film) {
        Optional<Film> updateById = filmStorage.updateById(id, film);

        if (updateById.isEmpty()) {
            throw new FilmNotFoundException("Несуществующий идентификатор фильма");
        }

        return updateById.get();
    }

    @Override
    public void deleteById(Long id) {
        filmStorage.delete(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        isUserIdPositive(userId);

        Optional<Film> filmStorageById = filmStorage.getById(id);

        if (filmStorageById.isEmpty()) {
            throw new FilmNotFoundException("Ошибка, данный фильм не найден.");
        }

        Film film = filmStorageById.get();
        film.setLikes(film.getLikes() + 1);

        Set<Long> usersWhoLike = film.getUsersWhoLike();

        if (usersWhoLike == null) {
            return;
        }

        usersWhoLike.add(userId);
        film.setUsersWhoLike(usersWhoLike);

        filmStorage.update(film);
    }

    private static void isUserIdPositive(Long userId) {
        if (userId < 0) {
            throw new FilmNotFoundException("Айди не может быть отрицательным");
        }
    }

    @Override
    public void removeLike(Long id, Long userId) {
        isUserIdPositive(userId);

        Optional<Film> filmStorageById = filmStorage.getById(id);

        if (filmStorageById.isEmpty()) {
            throw new FilmNotFoundException("Ошибка, данный фильм не найден.");
        }

        Film film = filmStorageById.get();

        if (film.getLikes() > 1) {
            film.setLikes(film.getLikes() - 1);

            Set<Long> usersWhoLike = film.getUsersWhoLike();

            if (usersWhoLike == null) {
                return;
            }

            usersWhoLike.remove(userId);
            film.setUsersWhoLike(usersWhoLike);

            filmStorage.update(film);
        }
    }

    @Override
    public List<Film> getPopularFilms(short count) {

        List<Film> all = filmStorage.getAll();

        if (all == null) {
            return new ArrayList<>();
        }

        List<Film> chartFilms = all.stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());

        return chartFilms;
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film getById(Long id) {
        Optional<Film> filmOptional = filmStorage.getById(id);

        if (filmOptional.isEmpty()) {
            throw new FilmNotFoundException("Фильма с таким айди нет.");
        }

        return filmOptional.get();
    }
}
