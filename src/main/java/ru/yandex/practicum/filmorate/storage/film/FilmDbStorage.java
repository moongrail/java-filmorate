package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final String INSERT_FILM = "INSERT INTO film(name,description,release_date,duration,rate)" +
            " VALUES (?,?,?,?,?)";
    private static final String FIND_ALL_FILMS = "SELECT f.film_id AS ID, f.name, f.RELEASE_DATE, F.DESCRIPTION," +
            " f.duration, f.rate, m.mpa_id, " +
            "mp.mpa_name FROM FILM F " +
            "LEFT JOIN FILM_MPA M ON F.FILM_ID = M.FILM_ID " +
            "LEFT JOIN MPA MP ON M.MPA_ID = MP.MPA_ID " +
            "ORDER BY F.FILM_ID ";

    private static final String FIND_FILMS_LIKED_BY_USER = "SELECT f.film_id AS ID, f.name, f.RELEASE_DATE, F.DESCRIPTION, " +
            "f.duration, f.rate, m.mpa_id, l.user_id, " +
            "mp.mpa_name FROM FILM F " +
            "LEFT JOIN FILM_MPA M ON F.FILM_ID = M.FILM_ID " +
            "LEFT JOIN MPA MP ON M.MPA_ID = MP.MPA_ID " +
            "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID  " +
            "WHERE user_id = ?" +
            "ORDER BY F.FILM_ID ";
    private static final String FIND_GENRES =
            "SELECT f.genre_id AS id, g.genre_name AS name " +
                    "FROM film_genre f " +
                    "LEFT JOIN  genre g ON f.genre_id = g.genre_id " +
                    "WHERE f.film_id = ? " +
                    "ORDER BY g.genre_id";
    private static final String FIND_DIRECTORS =
            "SELECT d.director_id AS id, d.director_name AS name " +
                    "FROM film_director fd " +
                    "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                    "WHERE fd.film_id = ? " +
                    "ORDER BY d.director_id";

    private static final String FIND_LIKES =
            "SELECT l.user_id " +
                    "FROM likes l " +
                    "WHERE l.film_id = ? ";
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
    private static final String GET_DIRECTORS = "SELECT * FROM directors AS d" +
            " JOIN film_director AS fd ON d.director_id = fd.director_id WHERE fd.film_id = ?";
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

            Set<Director> directors = film.getDirectors();
            if (!directors.isEmpty()) {
                deleteAllDirectorsFromFilm(idFilm);
            }

            addDirectorToFilm(film);

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

        Set<Director> directors = film.getDirectors();
        if (directors.isEmpty()) {
            deleteAllDirectorsFromFilm(filmId);
        }

        addDirectorToFilm(film);

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

            Set<Director> directors = new HashSet<>();

            SqlRowSet rsDirectors = jdbcTemplate.queryForRowSet(FIND_DIRECTORS, filmId);
            while (rsDirectors.next()) {
                Director director = Director.builder()
                        .id(rsDirectors.getLong("director_id"))
                        .name(rsDirectors.getString("director_name"))
                        .build();
                directors.add(director);

            }
            film.setDirectors(directors);

            SqlRowSet rsLikes = jdbcTemplate.queryForRowSet(FIND_LIKES, filmId);
            Set<Long> likes = new HashSet<>();
            while (rsLikes.next()) {
                likes.add(rsLikes.getLong("user_id"));
            }
            film.setUsersWhoLike(likes);

            films.put(filmId, film);
        }

        return films;
    }

    @Override
    public List<Film> getTheMostPopularFilms(int count) {
        ArrayList<Film> films = new ArrayList<>(getListOfFilms(FIND_TOP_FILMS, count).values());
        return films.stream().sorted(Comparator.comparing(Film::getRate).reversed()).collect(Collectors.toList());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Optional<Film> byId = getById(filmId);

        if (byId.isPresent()) {
            Film film = byId.get();
            film.setRate(film.getRate() + 1);
            update(film);
        }

        jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Optional<Film> byId = getById(filmId);

        if (byId.isPresent()) {
            Film film = byId.get();
            film.setRate(film.getRate() - 1);
            update(film);
        }

        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getFilmsLikedByUser(Long userId) {
        return new ArrayList<>(getListOfFilms(FIND_FILMS_LIKED_BY_USER, userId).values());
    }

    private void addDirectorToFilm(Film film) {
        String sql = "INSERT INTO film_director (director_id, film_id) VALUES (?, ?)";

        List<Director> directors = new ArrayList<>(film.getDirectors());

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, directors.get(i).getId());
                ps.setLong(2, film.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private boolean deleteAllDirectorsFromFilm(long filmId) {
        String sql = "DELETE FROM film_director WHERE film_id = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
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

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        String sql = "SELECT f.*, l.GENRE_ID, l.GENRE_NAME, m.*, likes.COUNT_LIKE, d.* " +
                "FROM DIRECTORS AS d " +
                "LEFT JOIN film_director fd ON fd.director_id = d.director_id " +
                "LEFT JOIN FILM AS f ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN FILM_GENRE AS g ON f.FILM_ID = g.FILM_ID " +
                "LEFT JOIN GENRE AS l ON g.GENRE_ID = l.GENRE_ID " +
                "LEFT JOIN FILM_MPA fm ON f.FILM_ID = fm.FILM_ID " +
                "LEFT JOIN MPA AS m ON fm.MPA_ID = m.MPA_ID " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(USER_ID) AS COUNT_LIKE FROM LIKES GROUP BY FILM_ID) AS likes ON " +
                "f.FILM_ID = likes.FILM_ID WHERE d.DIRECTOR_ID=? " +
                "ORDER BY likes.COUNT_LIKE DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rowMapperFilm(rs), directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(long directorId) {
        String sql = "SELECT f.*, l.GENRE_ID, l.GENRE_NAME, m.*, likes.COUNT_LIKE, d.*" +
                "FROM DIRECTORS AS d " +
                "LEFT JOIN film_director fd ON fd.director_id = d.director_id " +
                "LEFT JOIN FILM AS f ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN FILM_GENRE AS g ON f.FILM_ID = g.FILM_ID " +
                "LEFT JOIN GENRE AS l ON g.GENRE_ID = l.GENRE_ID " +
                "LEFT JOIN FILM_MPA fm ON f.FILM_ID = fm.FILM_ID " +
                "LEFT JOIN MPA AS m ON fm.MPA_ID = m.MPA_ID " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(USER_ID) AS COUNT_LIKE FROM LIKES GROUP BY FILM_ID) AS likes ON " +
                "f.FILM_ID = likes.FILM_ID WHERE d.DIRECTOR_ID= ? " +
                "ORDER BY f.RELEASE_DATE";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rowMapperFilm(rs), directorId);
    }

    private Film rowMapperFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int rate = rs.getInt("rate");
        Mpa mpa = new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name"));
        Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
        Director director = new Director(rs.getLong("director_id"), rs.getString("director_name"));

        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .rate(rate)
                .mpa(mpa)
                .build();

        if (genre.getId() != 0) {
            film.getGenres().add(genre);
        }

        if (director.getId() != 0) {
            film.getDirectors().add(director);
        }

        return film;
    }
}
