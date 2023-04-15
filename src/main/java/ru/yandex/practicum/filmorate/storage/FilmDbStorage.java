package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final String INSERT_FILM = "INSERT INTO film(name,description,release_date,duration,rating_id)" +
            " VALUES (?,?,?,?,?)";
    private static final String FIND_ALL_FILMS = "SELECT * FROM film";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM film WHERE film_id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM film WHERE film_id = ?";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, release_date = ?," +
            " duration = ?, rating_id = ? WHERE film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> save(Film film) {
        try {
            long idFilm = saveAndReturnId(film, INSERT_FILM);
            film.setId(idFilm);

            return Optional.of(film);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private long saveAndReturnId(Film film, String sql) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setString(5, film.getRatingId());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public List<Film> getAll() {
        List<Film> filmList = jdbcTemplate.query(FIND_ALL_FILMS, (rs, row) -> filmMapper(rs));
        return filmList;
    }

    @Override
    public Optional<Film> update(Film film) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("Select * from film where feilm_id = ?", film.getId());

        if (userRows.next()) {
            jdbcTemplate.update(UPDATE_FILM,
                    film.getName(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getLikes(),
                    film.getId());

            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> updateById(Long id, Film film) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(DELETE_BY_ID,id);
    }

    @Override
    public Optional<Film> getById(Long id) {
        SqlRowSet userRs = jdbcTemplate.queryForRowSet(FIND_FILM_BY_ID, id);
        if (userRs.next()) {
            Film film = jdbcTemplate.queryForObject(FIND_FILM_BY_ID, (rs, row) -> filmMapper(rs), id);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    private Film filmMapper(ResultSet rs) throws SQLException {
        String sqlLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        String sqlGenre = "SELECT genre FROM genre JOIN film_genre ON genre.id = film_genre.film_id " +
                "JOIN film AS f ON film_genre.film_id = f.id " +
                "WHERE film_id = ?";

        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer duration = rs.getInt("duration");
        long likes = rs.getLong("likes");
        LocalDate releasedate = rs.getDate("release_date").toLocalDate();
        String rating = rs.getString("rating_id");
        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .likes(likes)
                .releaseDate(releasedate)
                .ratingId(rating)
                .build();
        film.setUsersWhoLike(new HashSet<>(jdbcTemplate.query(sqlLikes, (rsLikes, rowNum) ->
                rsLikes.getLong("user_id"), id)));
        film.setGenres(new ArrayList<>(jdbcTemplate.query(sqlGenre, (rsGenre, rowNum)
                -> filmGenre(rsGenre), id)));
        return film;
    }

    private Genre filmGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .name(rs.getString("genre"))
                .build();
    }
}
