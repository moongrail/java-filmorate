package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;
    private final Gson gson = new Gson();

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
    public ResponseEntity<String> createDirector(@RequestBody @Valid Director director) {
        //проверка на валидацию

        directorService.addDirector(director);
        log.info("Создан режиссёр - {} ", director);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(director));
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("Обновлён режиссёр - {} ", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDirector(@PathVariable @Valid long id) {
        directorService.deleteDirector(id);
        log.info("Режиссёр удалён - {} ", id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Режиссёр удалён");
    }
}
