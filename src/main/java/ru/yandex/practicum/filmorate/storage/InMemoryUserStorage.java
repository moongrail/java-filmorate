package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        userStorage.put(++index, user);

        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        return userStorage.values()
                .stream()
                .toList();
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
}
