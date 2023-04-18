package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenresServiceImpl implements GenresService {
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    @Override
    public Genre getGenreById(Long id) {
        Optional<Genre> byId = genreStorage.getById(id);

        if (byId.isEmpty()) {
            throw new GenreNotFoundException("Genres not found");
        }

        return byId.get();
    }
}
