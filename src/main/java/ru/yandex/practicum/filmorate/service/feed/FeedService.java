package ru.yandex.practicum.filmorate.service.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedService {
    List<Feed> getFeeds(Long userId);

    void saveAddFriend(Long userId, Long friendId);

    void saveRemoveFriend(Long userId, Long friendId);

    void saveAddLike(Long userId, Long filmId);

    void saveRemoveLike(Long userId, Long filmId);

    void saveRemoveReview(Long userId, Long filmId);

    void saveUpdateReview(Long userId, Long filmId);

    void saveAddReview(Long userId, Long filmId);

    void saveAddLikeReview(Long userId, Long reviewId);

    void saveRemoveLikeReview(Long userId, Long reviewId);

    void saveAddDislikeReview(Long userId, Long reviewId);

    void saveRemoveDislikeReview(Long userId, Long reviewId);
}
