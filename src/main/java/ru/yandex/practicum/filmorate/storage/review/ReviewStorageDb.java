package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewStorageDb implements ReviewStorage {
    private static final String ADD_REVIEW = "INSERT INTO reviews" +
            " (content, is_positive, user_id, film_id, useful)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String GET_ALL_REVIEWS = "SELECT * FROM reviews";
    private static final String GET_REVIEW_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String DELETE_REVIEW_BY_ID = "DELETE FROM reviews WHERE review_id = ?";
    private static final String UPDATE_REVIEW_PARTS = "UPDATE reviews " +
            "SET content = ?, is_positive = ?" +
            "WHERE review_id = ?";
    private static final String GET_REVIEWS_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
    private static final String ADD_LIKE = "INSERT INTO review_likes (review_id, user_id) VALUES (?, ?)";
    private static final String ADD_DISLIKE = "INSERT INTO review_dislikes (review_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String REMOVE_DISLIKE = "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL = "UPDATE reviews " +
            "SET useful = ? " +
            "WHERE review_id = ?";
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> new Review(
            rs.getLong("review_id"),
            rs.getString("content"),
            rs.getBoolean("is_positive"),
            rs.getLong("user_id"),
            rs.getLong("film_id"),
            rs.getLong("useful")
    );

    @Override
    public Optional<Review> addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int update = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_REVIEW, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setLong(5, review.getUseful());
            return stmt;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        if (update == 0) {
            return Optional.empty();
        }
        return Optional.of(review);
    }

    @Override
    public List<Review> getAllReviews() {
        return jdbcTemplate.query(GET_ALL_REVIEWS, reviewRowMapper);
    }

    public Optional<Review> getReviewById(Long id) {
        try {
            Review review = jdbcTemplate.queryForObject(GET_REVIEW_BY_ID, new Object[]{id}, reviewRowMapper);
            return Optional.of(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteReviewById(Long id) {
        jdbcTemplate.update(DELETE_REVIEW_BY_ID, id);
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        jdbcTemplate.update(UPDATE_REVIEW_PARTS,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        Optional<Review> reviewById = getReviewById(review.getReviewId());

        if (reviewById.isEmpty()) {
            return Optional.empty();
        }

        return reviewById;
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        return jdbcTemplate.query(
                GET_REVIEWS_BY_FILM_ID,
                new Object[]{filmId, count},
                reviewRowMapper
        );
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        updateUseful(reviewId, true);
        jdbcTemplate.update(ADD_LIKE, reviewId, userId);
    }

    private void updateUseful(Long reviewId, boolean isIncrease) {
        Optional<Review> reviewById = getReviewById(reviewId);
        Review review = reviewById.get();
        long useful = review.getUseful();

        if (isIncrease) {
            useful++;
        } else {
            useful--;
        }

        jdbcTemplate.update(UPDATE_USEFUL, useful, reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        updateUseful(reviewId, false);
        jdbcTemplate.update(ADD_DISLIKE, reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        updateUseful(reviewId, false);
        jdbcTemplate.update(REMOVE_LIKE, reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        updateUseful(reviewId, true);
        jdbcTemplate.update(REMOVE_DISLIKE, reviewId, userId);
    }
}
