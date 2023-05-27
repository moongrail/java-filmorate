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
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToDirector(rs));
    }

    @Override
    public Director getDirectorById(long id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToDirector(rs), id); //id->director
        } catch (DataRetrievalFailureException e) {
            log.warn("Режиссёр с id {} не найден ", id);
            throw new DirectorNotFoundException("Режиссёр не найден" + id);
        }
    }

    @SneakyThrows
    @Override
    public Director addDirector(Director director) {
        String sql = "INSERT INTO directors(director_name) VALUES (?)";

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        long id = simpleJdbcInsert.executeAndReturnKey(director.directors()).longValue();
        director.setId(id);
        director.getFilms().forEach(film -> addFilmsDirector(id, film.getId()));
        log.info("Режиссёр {} сохранен ", director.getName());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET director_name = ? WHERE Director_id = ?";
        if (jdbcTemplate.update(sql, director.getName(), director.getId()) > 0) {
            deleteAllFilmsByDirector(director.getId());
            director.getFilms().forEach(film -> addFilmsDirector(director.getId(), film.getId()));
            return director;
        }
        log.warn("Режиссёр с id {} не найден ", director.getId());
        throw new DirectorNotFoundException("Режиссёр не найден" + director.getName());
    }

    @Override
    public void deleteDirector(long id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        if (getDirectorById(id) == null) {
            log.warn("Запрашиваемый режиссёр {} отсутствует и не может быть удалён", id);
            throw new DirectorNotFoundException("Режиссёр не найден" + id);
        }
        jdbcTemplate.update(sql, id);
        deleteAllFilmsByDirector(id);
        log.info("Режиссёр {}  удалён", id);
    }

    @Override
    public boolean addFilmsDirector(long directorId, long fimId) {
        String sql = "INSERT INTO film_director (director_id, film_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, directorId, fimId) > 0;
    }


    public boolean deleteAllFilmsByDirector(long directorId) {
        String sql = "DELETE FROM film_director WHERE director_id = ?";
        return jdbcTemplate.update(sql, directorId) > 0;
    }

    private Director mapRowToDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
