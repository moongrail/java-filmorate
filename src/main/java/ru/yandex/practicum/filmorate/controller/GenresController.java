package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenresService;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenresController {

    private final GenresService genresService;
    private final Gson gson = new Gson();

    @GetMapping
    public ResponseEntity<String> getGenres() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(genresService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getGenreById(@PathVariable Long id) {

        Genre genreById = genresService.getGenreById(id);

        log.info("Загружен жанр под номером - {}", id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(genreById));
    }
}
