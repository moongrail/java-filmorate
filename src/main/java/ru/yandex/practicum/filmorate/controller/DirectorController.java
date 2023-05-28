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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public ResponseEntity<String> getDirectors() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(directorService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getDirectorById(@PathVariable long id) {
        Director getDirectorById = directorService.getDirectorById(id);
        log.info("Получен режиссёр по id {} ", id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(getDirectorById));
    }

    @PostMapping
    public ResponseEntity<String> createDirector(@RequestBody @Valid Director director,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при создании режиссёра - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(director));

        }

        directorService.addDirector(director);
        log.info("Создан режиссёр - {} ", director);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(director));
    }

    @PutMapping
    public ResponseEntity<String> updateDirector(@RequestBody @Valid Director director,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при обновлении режиссёра - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(director));

        }
        directorService.updateDirector(director);
        log.info("Обновлён режиссёр - {} ", director);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(director));
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable @Valid long id) {
        directorService.deleteDirector(id);
        log.info("Режиссёр удалён - {} ", id);
    }

}
