package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> addReview(Review review);

    List<Review> getAllReviews();

    Optional<Review> getReviewById(Long id);

    void deleteReviewById(Long id);

    Optional<Review> updateReview(Review review);

    List<Review> getReviewsByFilmId(Long filmId, int count);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);
}
