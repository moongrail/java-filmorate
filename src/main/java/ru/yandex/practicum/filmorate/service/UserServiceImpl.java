package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User add(User film) {
        Optional<User> save = userStorage.save(film);

        if (save.isEmpty()) {
            throw new IncorrectParameterException("Такой пользователь уже существует");
        }

        return save.get();
    }

    @Override
    public User update(User user) {
        Optional<User> update = userStorage.update(user);

        if (update.isEmpty()) {
            throw new UserNotFoundException("Пользователя с таким айди не существует.");
        }

        return update.get();
    }

    @Override
    public User updateById(Long id, User user) {
        Optional<User> updateById = userStorage.updateById(id, user);

        if (updateById.isEmpty()) {
            throw new UserNotFoundException("Пользователя с таким айди не существует.");
        }

        return updateById.get();
    }

    @Override
    public void deleteById(Long id) {
        userStorage.delete(id);
    }

    @Override
    public void addFriend(Long idFrom, Long idTo) {
        Optional<User> from = userStorage.getById(idFrom);
        Optional<User> to = userStorage.getById(idTo);

        if (from.isPresent() && to.isPresent()) {
            User userFrom = from.get();
            User userTo = to.get();

            userFrom.getFriendsId().add(userTo.getId());
            userTo.getFriendsId().add(userFrom.getId());

            userStorage.update(userFrom);
            userStorage.update(userTo);
        } else {
            throw new UserNotFoundException("Пользователя с таким айди не существует.");
        }
    }

    @Override
    public void removeFriend(Long idFrom, Long idTo) {
        Optional<User> from = userStorage.getById(idFrom);
        Optional<User> to = userStorage.getById(idTo);

        if (from.isPresent() && to.isPresent()) {
            User userFrom = from.get();
            User userTo = to.get();

            if (userFrom.getFriendsId() == null || userTo.getFriendsId() == null) {
                return;
            }

            if (userFrom.getFriendsId().contains(to) && userTo.getFriendsId().contains(from)) {
                userFrom.getFriendsId().remove(userTo.getId());
                userTo.getFriendsId().remove(userFrom.getId());

                userStorage.update(userFrom);
                userStorage.update(userTo);
            }
        } else {
            throw new UserNotFoundException("Пользователя с таким айди нет в списке друзей.");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        Optional<User> user = userStorage.getById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователя с таким айди не существует.");
        }

        if (user.get().getFriendsId() == null) {
            return new ArrayList<>();
        }

        List<User> users = getFriendsList(user.get().getFriendsId());

        return users;
    }

    @Override
    public List<User> getMutualFriends(Long from, Long to) {
        Optional<User> fromUser = userStorage.getById(from);
        Optional<User> toUser = userStorage.getById(to);

        if (fromUser.isPresent() && toUser.isPresent()) {
            User userFrom = fromUser.get();
            User userTo = toUser.get();

            Set<Long> friendsIdFrom = new HashSet<>();
            Set<Long> friendsIdTo = new HashSet<>();
            friendsIdFrom.addAll(userFrom.getFriendsId());
            friendsIdTo.addAll(userTo.getFriendsId());

            if (friendsIdFrom == null || friendsIdTo == null) {
                return new ArrayList<>();
            }

            boolean isSizeMore = friendsIdFrom.size() > friendsIdTo.size();

            if (isSizeMore) {
                friendsIdFrom.retainAll(friendsIdTo);

                return getFriendsList(friendsIdFrom);
            } else {
                friendsIdTo.retainAll(friendsIdFrom);

                return getFriendsList(friendsIdTo);
            }
        }

        return new ArrayList<>();
    }

    private List<User> getFriendsList(Set<Long> friendsIdTo) {
        List<Long> longs = new ArrayList<>(friendsIdTo);

        List<User> users = new ArrayList<>();

        for (Long ids : longs) {
            Optional<User> byId = userStorage.getById(ids);

            if (byId.isPresent()) {
                users.add(byId.get());
            }
        }

        return users;
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User getById(Long id) {
        Optional<User> user = userStorage.getById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с таким айди %d нет.", id));
        }

        return user.get();
    }

}
