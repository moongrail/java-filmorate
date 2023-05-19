package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review addReview(Review review) {

        if (checkExistFilmAndUser(review)) {
            throw new UserNotFoundException("Ошибка при добавлении отзыва");
        } else if (review.getFilmId() < 0 || review.getUserId() < 0) {
            throw new UserNotFoundException("ID фильма или пользователя не найдены");
        }

        Optional<Review> newReview = reviewStorage.addReview(review);

        if (newReview.isEmpty()) {
            throw new IncorrectParameterException("Ошибка при добавлении отзыва");
        }

        return newReview.get();
    }

    private boolean checkExistFilmAndUser(Review review) {
        return userStorage.getById(review.getUserId()).isEmpty() || filmStorage.getById(review.getFilmId()).isEmpty();
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews().stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Review getReviewById(Long id) {
        Optional<Review> reviewById = reviewStorage.getReviewById(id);

        checkExistReview(reviewById);

        return reviewById.get();
    }

    @Override
    public void deleteReviewById(Long id) {
        reviewStorage.deleteReviewById(id);
    }

    @Override
    public Review updateReview(Review review) {
        Optional<Review> updateReview = reviewStorage.updateReview(review);

        checkExistReview(updateReview);

        return updateReview.get();
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        return reviewStorage.getReviewsByFilmId(filmId, count).stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        checkExistReview(reviewStorage.getReviewById(reviewId));

        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        checkExistReview(reviewStorage.getReviewById(reviewId));

        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        checkExistReview(reviewStorage.getReviewById(reviewId));

        reviewStorage.removeLike(reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        checkExistReview(reviewStorage.getReviewById(reviewId));

        reviewStorage.removeDislike(reviewId, userId);
    }

    private void checkExistReview(Optional<Review> reviewStorage) {
        if (reviewStorage.isEmpty()) {
            throw new ReviewNotFoundException("Ревью с данным id не найдено");
        }
    }
}
