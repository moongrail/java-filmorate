package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaStorageDb implements MpaStorage {
    private final static String FIND_ALL = "SELECT * FROM MPA";
    private final static String FIND_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query(FIND_ALL, this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM MPA WHERE MPA_ID = ?",
                Integer.class, id);

        if (count == 0) {
            return Optional.empty();
        } else if (count == 1) {
            Mpa mpa = jdbcTemplate.queryForObject(FIND_BY_ID, this::mapRowToMpa, id);
            return Optional.of(mpa);
        } else {
            throw new MpaNotFoundException("Not found");
        }
    }

    private Mpa mapRowToMpa(ResultSet rs, long rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    public boolean containsMpa(long id) {
        return jdbcTemplate.queryForRowSet(FIND_BY_ID, id).next();
    }
}
