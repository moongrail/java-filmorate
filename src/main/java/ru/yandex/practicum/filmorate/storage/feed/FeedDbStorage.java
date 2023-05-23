package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.time.Instant;
import java.util.List;

@Component
public class FeedDbStorage implements FeedStorage {

    private final static String SAVE_ACTION = "INSERT INTO feeds (timestamp, user_id, event_type, operation, entity_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private final static String GET_FEEDS = "SELECT * FROM feeds WHERE user_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void saveAddFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.FRIEND.toString(), Operation
                .ADD.toString(), friendId);
    }

    @Override
    public void saveRemoveFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.FRIEND.toString(),
                Operation.REMOVE.toString(), friendId);
    }

    @Override
    public void saveAddLike(Long userId, Long filmId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.LIKE.toString(), Operation
                .ADD.toString(), filmId);
    }

    @Override
    public void saveRemoveLike(Long userId, Long filmId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.LIKE.toString(), Operation
                .REMOVE.toString(), filmId);
    }

    @Override
    public void saveRemoveReview(Long userId, Long filmId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(), Operation
                .REMOVE.toString(), filmId);
    }

    @Override
    public void saveUpdateReview(Long userId, Long reviewId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(), Operation
                .UPDATE.toString(), reviewId);
    }

    @Override
    public void saveAddReview(Long userId, Long reviewId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(),
                Operation.ADD.toString(), reviewId);
    }

    @Override
    public void saveAddLikeReview(Long userId, Long reviewId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(),
                Operation.ADD.toString(), reviewId);
    }

    @Override
    public void saveRemoveLikeReview(Long userId, Long reviewId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(),
                Operation.REMOVE.toString(), reviewId);
    }

    @Override
    public void saveAddDislikeReview(Long userId, Long reviewId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(),
                Operation.ADD.toString(), reviewId);
    }

    @Override
    public void saveRemoveDislikeReview(Long userId, Long reviewId) {
        jdbcTemplate.update(SAVE_ACTION, Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(),
                Operation.REMOVE.toString(), reviewId);
    }

    @Override
    public List<Feed> getFeeds(Long userId) {
        return jdbcTemplate.query(GET_FEEDS, new FeedRowMapper(), userId);
    }
}
