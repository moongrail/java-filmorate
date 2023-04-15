MERGE INTO rating (RATING_ID, name) KEY (RATING_ID)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG13'),
           (4, 'R'),
           (5, 'NC17');

MERGE INTO genre (GENRE_ID, name) key (GENRE_ID)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');
