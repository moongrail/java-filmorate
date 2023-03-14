package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.BindingResultErrorsUtil;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static final Map<Long, User> USER_MAP = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public ResponseEntity<String> getFilms() {
        if (USER_MAP.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(USER_MAP.values().toString()));
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при создании пользователя - {}", bindingResult.getAllErrors());
            List<String> errors = BindingResultErrorsUtil.getErrors(bindingResult);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(errors.toString());

        } else if (USER_MAP.containsKey(user.getId())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Такой пользователь уже существует, попробуйте обновить данные о нём.");
        }

        log.info("Создан пользователь - {}", user);
        USER_MAP.put(user.getId(), user);

        setNameIfItEmpty(user);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(user));
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при обновлении пользователя - {}", bindingResult.getAllErrors());

            List<String> errors = BindingResultErrorsUtil.getErrors(bindingResult);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(errors.toString());

        } else if (!USER_MAP.containsKey(user.getId())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Такого пользователя не существует.");
        }

        log.info("Обновлен пользователь - {}", user);

        USER_MAP.put(user.getId(), user);

        setNameIfItEmpty(user);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(user));
    }

    private static void setNameIfItEmpty(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
