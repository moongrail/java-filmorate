package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private static Long index = 0L;
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public Optional<User> save(User user) {
        if (userStorage.containsKey(user.getId())) {
            return Optional.empty();
        }

        user.setId(++index);
        userStorage.put(user.getId(), user);

        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        return userStorage.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> update(User user) {
        if (!userStorage.containsKey(user.getId())) {
            return Optional.empty();
        }

        userStorage.put(user.getId(), user);

        return Optional.of(user);
    }

    @Override
    public Optional<User> updateById(Long id, User user) {
        if (!userStorage.containsKey(id)) {
            return Optional.empty();
        }

        user.setId(id);
        userStorage.put(id, user);

        return Optional.of(user);
    }

    @Override
    public void delete(Long id) {
        if (userStorage.containsKey(id)) {
            userStorage.remove(id);
        }
    }

    @Override
    public Optional<User> getById(Long id) {
        if (userStorage.containsKey(id)) {
            return Optional.of(userStorage.get(id));
        }
        return Optional.empty();
    }
}
