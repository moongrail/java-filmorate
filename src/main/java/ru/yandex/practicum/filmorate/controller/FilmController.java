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
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public ResponseEntity<String> getFilms() {
        if (filmService.getAll().isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

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
}
