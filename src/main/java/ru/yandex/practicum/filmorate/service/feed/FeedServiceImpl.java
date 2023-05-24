package ru.yandex.practicum.filmorate.service.feed;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {

    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    public FeedServiceImpl(FeedStorage feedStorage, UserStorage userStorage) {
        this.feedStorage = feedStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Feed> getFeeds(Long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с айди " + userId + " не найден");
        }

        return feedStorage.getFeeds(userId);
    }

    @Override
    public void saveAddFriend(Long userId, Long friendId) {
        feedStorage.saveAddFriend(userId, friendId);
    }

    @Override
    public void saveRemoveFriend(Long userId, Long friendId) {
        feedStorage.saveRemoveFriend(userId, friendId);
    }

    @Override
    public void saveAddLike(Long userId, Long filmId) {
        feedStorage.saveAddLike(userId, filmId);
    }

    @Override
    public void saveRemoveLike(Long userId, Long filmId) {
        feedStorage.saveRemoveLike(userId, filmId);
    }

    @Override
    public void saveRemoveReview(Long userId, Long reviewId) {
        feedStorage.saveRemoveReview(userId, reviewId);
    }

    @Override
    public void saveUpdateReview(Long userId, Long filmId) {
        feedStorage.saveUpdateReview(userId, filmId);
    }

    @Override
    public void saveAddReview(Long userId, Long filmId) {
        feedStorage.saveAddReview(userId, filmId);
    }

    @Override
    public void saveAddLikeReview(Long userId, Long reviewId) {
        feedStorage.saveAddLikeReview(userId, reviewId);
    }

    @Override
    public void saveRemoveLikeReview(Long userId, Long reviewId) {
        feedStorage.saveRemoveLikeReview(userId, reviewId);
    }

    @Override
    public void saveAddDislikeReview(Long userId, Long reviewId) {
        feedStorage.saveAddDislikeReview(userId, reviewId);
    }

    @Override
    public void saveRemoveDislikeReview(Long userId, Long reviewId) {
        feedStorage.saveRemoveDislikeReview(userId, reviewId);
    }
}
