package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public Optional<User> add(User film) {
        return userStorage.save(film);
    }

    @Override
    public Optional<User> update(User user) {
        Optional<User> update = userStorage.update(user);

        if (update.isEmpty()){
            throw new IncorrectParameterException("Пользователя с таким айди не существует.");
        }

        return update;
    }

    @Override
    public Optional<User> updateById(Long id, User user) {
        Optional<User> updateById = userStorage.updateById(id, user);

        if (updateById.isEmpty()){
            throw new IncorrectParameterException("Пользователя с таким айди не существует.");
        }

        return updateById;
    }

    @Override
    public void deleteById(Long id) {
        userStorage.delete(id);
    }

    @Override
    public void addFriend(Long idFrom, Long idTo) {
        Optional<User> from = userStorage.getById(idFrom);
        Optional<User> to = userStorage.getById(idTo);

        if (from.isPresent() && to.isPresent()){
            User userFrom = from.get();
            User userTo = to.get();

            Set<Long> friendsIdFrom = userFrom.getFriendsId();
            Set<Long> friendsIdTo = userTo.getFriendsId();
            friendsIdFrom.add(userTo.getId());
            friendsIdTo.add(userFrom.getId());

            userFrom.setFriendsId(friendsIdFrom);
            userTo.setFriendsId(friendsIdTo);

            userStorage.update(userFrom);
            userStorage.update(userTo);
        }
    }

    @Override
    public void removeFriend(Long idFrom, Long idTo) {
        Optional<User> from = userStorage.getById(idFrom);
        Optional<User> to = userStorage.getById(idTo);

        if (from.isPresent() && to.isPresent()){
            User userFrom = from.get();
            User userTo = to.get();

            Set<Long> friendsIdFrom = userFrom.getFriendsId();
            Set<Long> friendsIdTo = userTo.getFriendsId();

            if (friendsIdFrom.contains(to)){
                friendsIdFrom.remove(userTo.getId());
                friendsIdTo.remove(userFrom.getId());

                userFrom.setFriendsId(friendsIdFrom);
                userTo.setFriendsId(friendsIdTo);

                userStorage.update(userFrom);
                userStorage.update(userTo);
            }
        }

    }

    @Override
    public List<Long> getFriendsId(Long id) {
        Optional<User> user = userStorage.getById(id);

        return user.map(value -> new ArrayList<>(value.getFriendsId()))
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<Long> getMutualFriends(Long from, Long to) {
        Optional<User> fromUser = userStorage.getById(from);
        Optional<User> toUser = userStorage.getById(to);

        if (fromUser.isPresent() && toUser.isPresent()) {
            User userFrom = fromUser.get();
            User userTo = toUser.get();

            Set<Long> friendsIdFrom = userFrom.getFriendsId();
            Set<Long> friendsIdTo = userTo.getFriendsId();

            boolean isSizeMore = friendsIdFrom.size() > friendsIdTo.size();

            if (isSizeMore){
                friendsIdFrom.retainAll(friendsIdTo);
                return new ArrayList<>(friendsIdFrom);
            }else {
                friendsIdTo.retainAll(friendsIdFrom);
                return new ArrayList<>(friendsIdTo);
            }
        }

        return new ArrayList<>();
    }
}
