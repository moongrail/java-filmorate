package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director deleteDirector(long id);

    boolean addFilmsDirector(long directorId, long fimId);
}
