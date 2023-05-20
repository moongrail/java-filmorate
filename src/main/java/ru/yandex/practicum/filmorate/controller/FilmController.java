package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public ResponseEntity<String> getFilms() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(filmService.getAll()));
    }

    @PostMapping
    public ResponseEntity<String> createFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при создании фильма - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(film));

        }

        filmService.add(film);

        log.info("Создан фильм - {}", film);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(film));
    }

    @PutMapping
    public ResponseEntity<String> updateFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при обновлении фильма - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(film));

        }

        filmService.update(film);
        log.info("Обновлен фильм - {}", film);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> addLikeFilm(@PathVariable Long id,
                                              @PathVariable Long userId) {

        filmService.addLike(id,userId);
        log.info("Лайк поставлен фильм с айди - {}", id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson("Лайк поставлен."));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<String> removeLikeFilm(@PathVariable String id,
                                              @PathVariable String userId) {

        filmService.removeLike(Long.valueOf(id),Long.valueOf(userId));
        log.info("Лайк убран. фильм с айди - {}", id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson("Лайк убран."));
    }

    @GetMapping("/popular")
    public ResponseEntity<String> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Short count,
                                                  @RequestParam(required = false, defaultValue = "0") Long genreId,
                                                  @RequestParam(required = false, defaultValue = "0") Integer year) {
        log.info("GET request with parameters: count = {}, genreId = {}, date = {}", count, genreId, year);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(filmService.getPopularFilmsByParameters(count, genreId, year)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getFilmById(@PathVariable String id) {

        Film film = filmService.getFilmFull(Long.valueOf(id));
        log.info("Выдан фильм с айди - {}", id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(film));
    }
}
