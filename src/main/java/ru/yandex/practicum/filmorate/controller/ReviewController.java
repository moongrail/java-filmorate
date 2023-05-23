package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final FeedService feedService;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody @Valid Review review, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при создании ревью - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(review));

        }

        Review addedReview = reviewService.addReview(review);
        feedService.saveAddReview(review.getUserId(), review.getReviewId());
        log.info("Добавлен отзыв с id {}", addedReview.getReviewId());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(addedReview));
    }

    @PutMapping()
    public ResponseEntity<String> updateReview(@RequestBody @Valid Review review, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Ошибки валидации при обновлении ревью - {}", bindingResult.getAllErrors());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(review));

        }

        Review updatedReview = reviewService.updateReview(review);
        feedService.saveUpdateReview(review.getUserId(), review.getReviewId());
        log.info("Обновлен отзыв с id {}", updatedReview.getReviewId());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(updatedReview));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);

        reviewService.deleteReviewById(id);
        feedService.saveRemoveReview(review.getUserId(), review.getReviewId());
        log.info("Удален отзыв с id {}", id);

        return ResponseEntity.ok("Отзыв удален");
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<String> getReviewById(@PathVariable Long reviewId) {
        Review review = reviewService.getReviewById(reviewId);
        log.info("Получен отзыв с id {}", reviewId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(review));
    }

    @GetMapping
    public ResponseEntity<String> getReviewsByFilmId(@RequestParam(required = false) Long filmId,
                                                     @RequestParam(required = false, defaultValue = "10") int count) {
        List<Review> reviews;

        if (filmId == null) {
            reviews = reviewService.getAllReviews();
        } else {
            reviews = reviewService.getReviewsByFilmId(filmId, count);
        }
        log.info("Получено {} отзывов", reviews.size());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(reviews));
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        Review review = reviewService.getReviewById(reviewId);

        reviewService.addLike(reviewId, userId);
        //feedService.saveAddLikeReview(userId, review.getFilmId());
        log.info("Пользователь с id {} поставил лайк отзыву с id {}", userId, reviewId);

        return ResponseEntity.ok("Лайк добавлен");
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public ResponseEntity<String> addDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.addDislike(reviewId, userId);
        //feedService.saveAddDislikeReview(userId, reviewId);
        log.info("Пользователь с id {} поставил дизлайк отзыву с id {}", userId, reviewId);

        return ResponseEntity.ok("Дизлайк добавлен");
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public ResponseEntity<String> removeLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.removeLike(reviewId, userId);
        feedService.saveRemoveLikeReview(userId, reviewId);
        log.info("Пользователь с id {} удалил лайк отзыва с id {}", userId, reviewId);

        return ResponseEntity.ok("Лайк удален");
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public ResponseEntity<String> removeDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.removeDislike(reviewId, userId);
        //feedService.saveRemoveDislikeReview(userId, reviewId);
        log.info("Пользователь с id {} удалил дизлайк отзыва с id {}", userId, reviewId);

        return ResponseEntity.ok("Дизлайк удален");
    }
}
