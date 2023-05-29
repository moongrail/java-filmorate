package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@Qualifier("DirectorStorageDb")
@RequiredArgsConstructor
public class DirectorStorageDb implements DirectorStorage {
    public static final String GET_ALL_DIRECTORS = "SELECT * FROM directors";
    public static final String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    public static final String UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE Director_id = ?";
    public static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    public static final String ADD_FILMS_DIRECTOR = "INSERT INTO film_director (director_id, film_id) VALUES (?, ?)";
    public static final String DELETE_ALL_FILMS_BY_DIRECTOR = "DELETE FROM film_director WHERE director_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(GET_ALL_DIRECTORS, (rs, rowNum) -> mapRowToDirector(rs));
    }

    @Override
    public Director getDirectorById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_DIRECTOR_BY_ID, (rs, rowNum) -> mapRowToDirector(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Режиссёр с id {} не найден ", id);
            throw new DirectorNotFoundException("Режиссёр не найден" + id);
        }
    }

    @SneakyThrows
    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        long id = simpleJdbcInsert.executeAndReturnKey(director.directors()).longValue();
        director.setId(id);
        log.info("Режиссёр {} сохранен ", director.getName());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (jdbcTemplate.update(UPDATE_DIRECTOR, director.getName(), director.getId()) > 0) {
            return director;
        }
        log.warn("Режиссёр с id {} не найден ", director.getId());
        throw new DirectorNotFoundException("Режиссёр не найден" + director.getName());
    }

    @Override
    public void deleteDirector(long id) {
        if (getDirectorById(id) == null) {
            log.warn("Запрашиваемый режиссёр {} отсутствует и не может быть удалён", id);
            throw new DirectorNotFoundException("Режиссёр не найден" + id);
        }
        jdbcTemplate.update(DELETE_DIRECTOR, id);
        deleteAllFilmsByDirector(id);
        log.info("Режиссёр {}  удалён", id);
    }

    @Override
    public boolean addFilmsDirector(long directorId, long fimId) {
        return jdbcTemplate.update(ADD_FILMS_DIRECTOR, directorId, fimId) > 0;
    }


    public boolean deleteAllFilmsByDirector(long directorId) {
        return jdbcTemplate.update(DELETE_ALL_FILMS_BY_DIRECTOR, directorId) > 0;
    }

    private Director mapRowToDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
