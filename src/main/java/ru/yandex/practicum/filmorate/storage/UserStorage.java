package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(User user);

    List<User> getAll();

    Optional<User> update(User user);

    Optional<User> updateById(Long id, User user);

    void delete(Long id);

    Optional<User> getById(Long id);

}
