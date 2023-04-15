package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User add(User user);

    User update(User user);

    User updateById(Long id, User film);

    void deleteById(Long id);

    void addFriend(Long idFrom, Long idTo);

    void removeFriend(Long idFrom, Long idTo);

    List<User> getFriends(Long id);

    List<User> getMutualFriends(Long from, Long to);

    List<User> getAll();

    User getById(Long id);
}
