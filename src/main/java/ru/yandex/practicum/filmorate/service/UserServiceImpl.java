package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public Optional<User> add(User film) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User film) {
        return Optional.empty();
    }

    @Override
    public Optional<User> updateById(Long id, User film) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void addFriend(Long idFrom, Long idTo) {

    }

    @Override
    public void removeFriend(Long idFrom, Long idTo) {

    }

    @Override
    public List<User> getFriends(Long id) {
        return null;
    }

    @Override
    public List<User> getMutualFriends(Long from, Long to) {
        return null;
    }
}
