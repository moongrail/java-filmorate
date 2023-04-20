package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreStorageDb implements GenreStorage {
    private static final String FIND_ALL = "SELECT * FROM genre";
    private static final String FIND_BY_ID = "SELECT * FROM genre WHERE genre_id=?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query(FIND_ALL, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM GENRE WHERE GENRE_ID = ?",
                Integer.class, id);
        if (count == 0) {
            return Optional.empty();
        } else if (count == 1) {
            Genre genre = jdbcTemplate.queryForObject(FIND_BY_ID, this::mapRowToGenre, id);
            return Optional.of(genre);
        } else {
            throw new GenreNotFoundException("Not found");
        }
    }

    private Genre mapRowToGenre(ResultSet rs, long rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
