package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.BindingResultErrorsUtil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final Map<Long, Film> FILMS_MAP = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public ResponseEntity<String> getFilms() {
        if (FILMS_MAP.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(FILMS_MAP.values()));
    }

    @PostMapping
    public ResponseEntity<String> createFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при создании фильма - {}", bindingResult.getAllErrors());

            List<String> errors = BindingResultErrorsUtil.getErrors(bindingResult);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(errors.toString());

        } else if (FILMS_MAP.containsKey(film.getId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Такой фильм уже существует, попробуйте обновить данные о нём.");

        }

        log.info("Создан фильм - {}", film);
        FILMS_MAP.put(film.getId(), film);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(film));
    }

    @PutMapping
    public ResponseEntity<String> updateFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при обновлении фильма - {}", bindingResult.getAllErrors());

            List<String> errors = BindingResultErrorsUtil.getErrors(bindingResult);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(errors.toString());

        } else if (!FILMS_MAP.containsKey(film.getId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Такого фильма не существует.");
        }

        log.info("Обновлен фильм - {}", film);
        FILMS_MAP.put(film.getId(), film);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(film));
    }
}
