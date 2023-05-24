package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void saveAddFriend(Long userId, Long friendId);

    void saveRemoveFriend(Long userId, Long friendId);

    void saveAddLike(Long userId, Long filmId);

    void saveRemoveLike(Long userId, Long filmId);

    void saveRemoveReview(Long userId, Long reviewId);

    void saveUpdateReview(Long userId, Long reviewId);

    void saveAddReview(Long userId, Long reviewId);

    void saveAddLikeReview(Long userId, Long reviewId);

    void saveRemoveLikeReview(Long userId, Long reviewId);

    void saveAddDislikeReview(Long userId, Long reviewId);

    void saveRemoveDislikeReview(Long userId, Long reviewId);

    List<Feed> getFeeds(Long userId);
}
