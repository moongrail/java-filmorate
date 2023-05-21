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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public ResponseEntity<List<User>> getFilms() {
        List<User> all = userService.getAll();

        if (all.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(all);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при создании пользователя - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(user));

        }

        userService.add(user);
        log.info("Создан пользователь - {}", user);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(user));
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при обновлении пользователя - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(gson.toJson(user));

        }

        log.info("Обновлен пользователь - {}", user);

        userService.update(user);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        log.info("Выдан пользователь с айди - {}", user);


        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(user));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> addToFriends(@PathVariable Long id,
                                               @PathVariable Long friendId) {


        userService.addFriend(id, friendId);
        log.info("Добавлен друг {} пользователю {}", friendId, id);


        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Друг добавлен.");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> removeFromFriends(@PathVariable Long id,
                                                    @PathVariable Long friendId) {

        userService.removeFriend(id, friendId);
        log.info("Удалён друг {} пользователю {}", friendId, id);


        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Друг удалён.");
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<String> getUserFriends(@PathVariable String id) {
        List<User> friendsId = userService.getFriends(Long.valueOf(id));
        log.info("Выданы айди друзей пользователя с айди - {}", id);


        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(friendsId));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<String> getUserFriends(@PathVariable String id, @PathVariable String otherId) {
        List<User> mutualFriends = userService.getMutualFriends(Long.valueOf(id), Long.valueOf(otherId));
        log.info("Общие друзья пользователя {} и {}", id, otherId);


        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(mutualFriends));
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        return userService.getRecommendations(id);
    }
}
