package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> add(User film);

    Optional<User> update(User film);

    Optional<User> updateById(Long id, User film);

    void deleteById(Long id);

    void addFriend(Long idFrom, Long idTo);

    void removeFriend(Long idFrom, Long idTo);

    List<Long> getFriendsId(Long id);

    List<Long> getMutualFriends(Long from, Long to);
}
