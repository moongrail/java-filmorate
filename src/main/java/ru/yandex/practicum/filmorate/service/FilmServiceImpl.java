package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    @Override
    public Optional<Film> add(Film film) {
        return Optional.empty();
    }

    @Override
    public Optional<Film> update(Film film) {
        return Optional.empty();
    }

    @Override
    public Optional<Film> updateById(Long id, Film film) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void addLike(Long userId) {

    }

    @Override
    public void removeLike(Long userId) {

    }

    @Override
    public List<Film> getPopularFilms(short count) {
        return null;
    }
}
