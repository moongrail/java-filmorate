package ru.yandex.practicum.filmorate.storage.film;

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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
@Primary
public class FilmDbStorage extends InMemoryFilmStorage {
    private static final String INSERT_FILM = "INSERT INTO film(name,description,release_date,duration,rate)" +
            " VALUES (?,?,?,?,?)";
    private static final String FIND_ALL_FILMS = "SELECT f.film_id AS ID, f.name, f.RELEASE_DATE, F.DESCRIPTION," +
            " f.duration, f.rate, m.mpa_id, " +
            "mp.mpa_name FROM FILM F " +
            "LEFT JOIN FILM_MPA M ON F.FILM_ID = M.FILM_ID " +
            "LEFT JOIN MPA MP ON M.MPA_ID = MP.MPA_ID " +
            "ORDER BY F.FILM_ID ";
    private static final String FIND_GENRES =
            "SELECT f.genre_id AS id, g.genre_name AS name " +
                    "FROM film_genre f " +
                    "LEFT JOIN  genre g ON f.genre_id = g.genre_id " +
                    "WHERE f.film_id = ? " +
                    "ORDER BY g.genre_id";
    private static final String FIND_TOP_FILMS = "SELECT F.FILM_ID AS ID, F.NAME, F.RELEASE_DATE, F.DESCRIPTION," +
            " F.DURATION, F.RATE, COUNT(L.USER_ID) AS liked, M.MPA_ID, MP.MPA_NAME " +
            "FROM FILM F " +
            "LEFT JOIN FILM_MPA M ON F.FILM_ID = M.FILM_ID " +
            "LEFT JOIN MPA MP ON M.MPA_ID = MP.MPA_ID " +
            "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID  " +
            "GROUP BY F.FILM_ID " +
            "ORDER BY LIKED DESC LIMIT ?";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM film WHERE film_id = ?";
    private static final String FIND_FILM_FULL =
            "SELECT F.FILM_ID  AS ID, F.NAME, F.RELEASE_DATE, F.DESCRIPTION, F.DURATION, F.RATE, " +
                    "M.mpa_id, MP.MPA_NAME  FROM FILM F " +
                    "LEFT JOIN FILM_MPA M ON F.FILM_ID = M.FILM_ID " +
                    "LEFT JOIN MPA MP ON M.MPA_ID = MP.mpa_id " +
                    "WHERE F.FILM_ID=? ";
    private static final String INSERT_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id=? AND user_id=? ";
    private static final String DELETE_BY_ID = "DELETE FROM film WHERE film_id = ?";
    private static final String DELETE_FILM_GENRE = "DELETE FROM film_genre WHERE film_id=? AND genre_id=? ";
    private static final String DELETE_FILM_RATING = "DELETE FROM film_mpa WHERE film_id=? AND mpa_id=? ";
    private static final String INSERT_FILM_RATING = "INSERT INTO FILM_mpa (film_id, mpa_id) VALUES (?,?)";
    private static final String INSERT_FILM_GENRE = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?,?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, release_date = ?," +
            " duration = ?, rate = ? WHERE film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> save(Film film) {
        try {
            Long idFilm = saveAndReturnId(film, INSERT_FILM);
            film.setId(idFilm);
            Mpa mpa = film.getMpa();
            updateFilmMpa(mpa.getId(), idFilm);
            Set<Genre> genres = film.getGenres();

            if (!genres.isEmpty()) {
                updateFilmGenres(removeDoubles(genres), idFilm);
            }

            return Optional.of(film);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Long saveAndReturnId(Film film, String sql) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getRate());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Film getFilmFull(Long id) {
        return getListOfFilms(FIND_FILM_FULL, id).get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(getListOfFilms(FIND_ALL_FILMS, 0).values());
    }

    @Override
    public Optional<Film> update(Film film) {
        if (getFilmFull(film.getId()) == null) {
            return Optional.empty();
        }

        long filmId = film.getId();
        jdbcTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), filmId);
        Film filmBefore = getFilmFull(filmId);
        Mpa mpaBefore = filmBefore.getMpa();
        Mpa mpaAfter = film.getMpa();

        if (!Objects.equals(mpaAfter.getId(), mpaBefore.getId())) {
            removeFilmMpa(mpaBefore, filmId);
            updateFilmMpa(mpaAfter.getId(), filmId);
        }

        Set<Genre> genresBefore = filmBefore.getGenres();
        Set<Long> genresAfter = removeDoubles(film.getGenres());

        if (!genresBefore.isEmpty()) {
            removeFilmGenres(genresBefore, filmId);
        }

        if (!genresAfter.isEmpty()) {
            updateFilmGenres(genresAfter, filmId);
        }

        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateById(Long id, Film film) {
        return Optional.empty();
    }


    @Override
    public void delete(Long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public Optional<Film> getById(Long id) {
        SqlRowSet userRs = jdbcTemplate.queryForRowSet(FIND_FILM_FULL, id);

        if (userRs.next()) {
            Film film = getListOfFilms(FIND_FILM_FULL, id).get(id);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    private Map<Long, Film> getListOfFilms(String sql, long id) {
        Map<Long, Film> films = new LinkedHashMap<>();
        SqlRowSet rs;

        if (id == 0) {
            rs = jdbcTemplate.queryForRowSet(sql);
        } else {
            rs = jdbcTemplate.queryForRowSet(sql, id);
        }

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = Film.builder()
                    .id(filmId)
                    .name(rs.getString("name"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .rate(rs.getLong("rate"))
                    .build();

            Mpa mpa = Mpa.builder()
                    .id(rs.getLong("mpa_id"))
                    .name(rs.getString("mpa_name"))
                    .build();

            film.setMpa(mpa);
            Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

            SqlRowSet rsGenres = jdbcTemplate.queryForRowSet(FIND_GENRES, filmId);
            while (rsGenres.next()) {
                Genre genre = Genre.builder()
                        .id(rsGenres.getLong("genre_id"))
                        .name(rsGenres.getString("genre_name"))
                        .build();
                genres.add(genre);
            }
            film.setGenres(genres);
            films.put(filmId, film);
        }

        return films;
    }

    public List<Film> getTheMostPopularFilms(int count) {
        ArrayList<Film> films = new ArrayList<>(getListOfFilms(FIND_TOP_FILMS, count).values());
        return films.stream().sorted(Comparator.comparing(Film::getRate).reversed()).collect(Collectors.toList());
    }

    public void addLike(Long filmId, Long userId) {
        Optional<Film> byId = getById(filmId);

        if (byId.isPresent()) {
            Film film = byId.get();
            film.setRate(film.getRate() + 1);
            update(film);
        }

        jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Optional<Film> byId = getById(filmId);

        if (byId.isPresent()) {
            Film film = byId.get();
            film.setRate(film.getRate() - 1);
            update(film);
        }

        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    public boolean containsFilm(long id) {
        return jdbcTemplate.queryForRowSet(FIND_FILM_BY_ID, id).next();
    }

    private Set<Long> removeDoubles(Set<Genre> genres) {
        return genres.stream().map(Genre::getId).collect(Collectors.toSet());
    }

    private boolean updateFilmMpa(Long mpaId, Long filmId) {
        return jdbcTemplate.update(INSERT_FILM_RATING, filmId, mpaId) > 0;
    }

    private void updateFilmGenres(Set<Long> genreIds, long filmId) {
        genreIds.forEach(g -> jdbcTemplate.update(INSERT_FILM_GENRE, filmId, g));
    }

    private boolean removeFilmMpa(Mpa mpa, long filmId) {
        return jdbcTemplate.update(DELETE_FILM_RATING, filmId, mpa.getId()) > 0;
    }

    private void removeFilmGenres(Set<Genre> genres, long filmId) {
        genres.forEach(genre -> jdbcTemplate.update(DELETE_FILM_GENRE, filmId, genre.getId()));
    }
}
