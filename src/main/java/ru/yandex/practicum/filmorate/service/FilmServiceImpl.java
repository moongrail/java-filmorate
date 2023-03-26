package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    @Override
    public Optional<Film> add(Film film) {
        return filmStorage.save(film);
    }

    @Override
    public Optional<Film> update(Film film) {
        Optional<Film> update = filmStorage.update(film);

        if (update.isEmpty()){
            throw new IncorrectParameterException("Несуществующий идентификатор фильма");
        }

        return update;
    }

    @Override
    public Optional<Film> updateById(Long id, Film film) {
        Optional<Film> updateById = filmStorage.updateById(id, film);

        if (updateById.isEmpty()){
            throw new IncorrectParameterException("Несуществующий идентификатор фильма");
        }

        return updateById;
    }

    @Override
    public void deleteById(Long id) {
        filmStorage.delete(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        Optional<Film> filmStorageById = filmStorage.getById(id);

        if (filmStorageById.isPresent()) {
            Film film = filmStorageById.get();
            film.setLikes(film.getLikes() + 1);

            Set<Long> usersWhoLike = film.getUsersWhoLike();
            usersWhoLike.add(userId);
            film.setUsersWhoLike(usersWhoLike);

            filmStorage.update(film);
        }
    }

    @Override
    public void removeLike(Long id, Long userId) {
        Optional<Film> filmStorageById = filmStorage.getById(id);

        if (filmStorageById.isPresent()) {
            Film film = filmStorageById.get();

            if (film.getLikes() > 1) {
                film.setLikes(film.getLikes() - 1);

                Set<Long> usersWhoLike = film.getUsersWhoLike();
                usersWhoLike.remove(userId);
                film.setUsersWhoLike(usersWhoLike);

                filmStorage.update(film);
            }
        }
    }

    @Override
    public List<Film> getPopularFilms(short count) {
        List<Film> chartFilms = filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikes))
                .limit(count)
                .collect(Collectors.toList());

        return chartFilms;
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }
}
