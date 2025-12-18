# Описание ER-диаграммы для java-filmorate

### Сама диаграмма:
![ER-диаграмма filmorate](ER-diagram/img/Database-ER-diagram-(filmorate).png)
## Таблицы
- film - хранит данные о фильмах
- user - хранит данные о пользователях
- genre - хранит данные о жанрах фильмов
- rating - хранит данные возрастных рейтингов фильмов
- friendship_status - хранит данные о статусе дружбы
- friendships - хранит данные о дружбе пользователей
- film_likes - хранит внешние ключи таблиц user и film для определения пользователей, которым фильм понравился
- films_geners - хранит внешние ключи таблиц genre и film для определения у фильма нескольких жанров

## Описание таблиц
1. Таблица пользователей
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
      USER_ID INTEGER NOT NULL AUTO_INCREMENT,
      EMAIL VARCHAR_IGNORECASE(255) NOT NULL,
      LOGIN VARCHAR_IGNORECASE(255) NOT NULL,
      NAME VARCHAR_IGNORECASE(255),
      BIRTHDAY DATE NOT NULL,
   CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
   );
   ```
2. Таблица рейтингов фильмов
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.RATING_MPA (
      RATING_ID INTEGER NOT NULL AUTO_INCREMENT,
      NAME VARCHAR_IGNORECASE(255) NOT NULL,
   CONSTRAINT RATING_MPA_PK PRIMARY KEY (RATING_ID)
   );
   ```
3. Таблица фильмов
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.FILM (
      FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
      DESCRIPTION VARCHAR_IGNORECASE(1000) NOT NULL,
      NAME VARCHAR_IGNORECASE(255) NOT NULL,
      RELEAS_DATE DATE NOT NULL,
      DURATION INTEGER NOT NULL,
      RATING_MPA_ID INTEGER,
   
   CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID),
   CONSTRAINT FILM_RATING_MPA_FK FOREIGN KEY (RATING_MPA_ID)
   REFERENCES PUBLIC.RATING_MPA(RATING_ID)
   ON DELETE SET NULL ON UPDATE CASCADE
   );
   
   CREATE INDEX IF NOT EXISTS FILM_RATING_MPA_FK_INDEX ON PUBLIC.FILM (RATING_MPA_ID);
   ```
4. Таблица жанров
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
      GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
      NAME VARCHAR_IGNORECASE(255) NOT NULL,
   CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
   );
   ```
5. Связь фильмов и жанров (many-to-many)
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.FILMS_GENRES (
      GENRE_ID INTEGER NOT NULL,
      FILM_ID INTEGER NOT NULL,
   
   CONSTRAINT FILMS_GENRES_FILM_FK FOREIGN KEY (FILM_ID)
   REFERENCES PUBLIC.FILM(FILM_ID)
   ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT FILMS_GENRES_GENRE_FK FOREIGN KEY (GENRE_ID)
   REFERENCES PUBLIC.GENRE(GENRE_ID)
   ON DELETE CASCADE ON UPDATE CASCADE
   );

   CREATE INDEX IF NOT EXISTS FILMS_GENRES_FILM_FK_INDEX ON PUBLIC.FILMS_GENRES (FILM_ID);
   CREATE INDEX IF NOT EXISTS FILMS_GENRES_GENRE_FK_INDEX ON PUBLIC.FILMS_GENRES (GENRE_ID);
   ```
6. Лайки фильмов (many-to-many)
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.FILM_LIKES (
      FILM_ID INTEGER NOT NULL,
      USER_ID INTEGER NOT NULL,
   
   CONSTRAINT FILM_LIKES_FILM_FK FOREIGN KEY (FILM_ID)
   REFERENCES PUBLIC.FILM(FILM_ID)
   ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT FILM_LIKES_USER_FK FOREIGN KEY (USER_ID)
   REFERENCES PUBLIC.USERS(USER_ID)
   ON DELETE CASCADE ON UPDATE CASCADE
   );
   
   CREATE INDEX IF NOT EXISTS FILM_LIKES_FILM_FK_INDEX ON PUBLIC.FILM_LIKES (FILM_ID);
   CREATE INDEX IF NOT EXISTS FILM_LIKES_USER_FK_INDEX ON PUBLIC.FILM_LIKES (USER_ID);
   ```
7. Статусы дружбы
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDSHIP_STATUS (
      FRIENDSHIP_STATUS_ID INTEGER NOT NULL AUTO_INCREMENT,
      NAME VARCHAR_IGNORECASE(255) NOT NULL,
      IS_ACCEPTED BOOLEAN NOT NULL,
   
   CONSTRAINT FRIENDSHIP_STATUS_PK PRIMARY KEY (FRIENDSHIP_STATUS_ID)
   );
   ```
8. Таблица дружбы (many-to-many с атрибутом статуса)
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDSHIP (
      USER_ID INTEGER NOT NULL,
      FRIEND_ID INTEGER NOT NULL,
      FRIENDSHIP_STATUS_ID INTEGER NOT NULL,

    CONSTRAINT FRIENDSHIP_PK PRIMARY KEY (USER_ID, FRIEND_ID),

    CONSTRAINT FRIENDSHIP_USER_FK FOREIGN KEY (USER_ID)
        REFERENCES PUBLIC.USERS(USER_ID)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT FRIENDSHIP_FRIEND_FK FOREIGN KEY (FRIEND_ID)
        REFERENCES PUBLIC.USERS(USER_ID)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT FRIENDSHIP_STATUS_FK FOREIGN KEY (FRIENDSHIP_STATUS_ID)
        REFERENCES PUBLIC.FRIENDSHIP_STATUS(FRIENDSHIP_STATUS_ID)
        ON DELETE SET NULL ON UPDATE CASCADE);
   );
   ```

9. Таблица ревью
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW (
      REVIEW_ID INTEGER NOT NULL AUTO_INCREMENT,
      CONTENT VARCHAR(500) NOT NULL,
      IS_POSITIVE BOOLEAN NOT NULL,
      USER_ID INTEGER NOT NULL,
      FILM_ID INTEGER NOT NULL,
      USEFUL INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT REVIEW_PK PRIMARY KEY (REVIEW_ID),

    CONSTRAINT REVIEW_USER_FK FOREIGN KEY (USER_ID)
        REFERENCES PUBLIC.USERS(USER_ID)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT REVIEW_FILM_FK FOREIGN KEY (FILM_ID)
        REFERENCES PUBLIC.FILM(FILM_ID)
        ON DELETE CASCADE ON UPDATE CASCADE
   );

   CREATE INDEX IF NOT EXISTS REVIEW_USER_FK_INDEX ON PUBLIC.REVIEW (USER_ID);
   CREATE INDEX IF NOT EXISTS REVIEW_FILM_FK_INDEX ON PUBLIC.REVIEW (FILM_ID);
   ```
10. Таблица оценок ревью
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW_REACTIONS (
       REVIEW_ID INTEGER NOT NULL,
       USER_ID INTEGER NOT NULL,
       IS_LIKE BOOLEAN NOT NULL,
   
       -- один отзыв и много реакций пользователей
       CONSTRAINT REVIEW_REACTIONS_PK PRIMARY KEY (REVIEW_ID, USER_ID),
   
       CONSTRAINT REVIEW_REACTIONS_REVIEW_FK FOREIGN KEY (REVIEW_ID)
           REFERENCES PUBLIC.REVIEW(REVIEW_ID)
           ON DELETE CASCADE,
   
       CONSTRAINT REVIEW_REACTIONS_USER_FK FOREIGN KEY (USER_ID)
           REFERENCES PUBLIC.USERS(USER_ID)
           ON DELETE CASCADE ON UPDATE CASCADE
   );
   ```
11. Таблица событий
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.EVENTS (
       EVENT_ID INTEGER NOT NULL AUTO_INCREMENT,
       USER_ID INTEGER NOT NULL,
       EVENT_TYPE VARCHAR(10) NOT NULL,
       OPERATION VARCHAR(10) NOT NULL,
       ENTITY_ID INTEGER NOT NULL,
       EVENT_TIMESTAMP BIGINT NOT NULL,
   
       CONSTRAINT EVENTS_PK PRIMARY KEY (EVENT_ID),
       CONSTRAINT EVENTS_USER_FK FOREIGN KEY (USER_ID)
           REFERENCES PUBLIC.USERS(USER_ID)
           ON DELETE CASCADE
   );
   ```
12. Таблица режиссеров
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.DIRECTORS (
       DIRECTOR_ID INTEGER NOT NULL AUTO_INCREMENT,
       NAME VARCHAR_IGNORECASE(200) NOT NULL,
       CONSTRAINT DIRECTOR_PK PRIMARY KEY (DIRECTOR_ID)
   );
   ```
13. Таблица режиссеров и фильмов
   ```SQL
   CREATE TABLE IF NOT EXISTS PUBLIC.DIRECTORS_FILMS (
       FILM_ID INTEGER NOT NULL,
       DIRECTOR_ID INTEGER NOT NULL,
       CONSTRAINT DIRECTORS_FILMS_FILM_FK FOREIGN KEY (FILM_ID)
           REFERENCES PUBLIC.FILM(FILM_ID)
           ON DELETE CASCADE ON UPDATE CASCADE,
       CONSTRAINT DIRECTORS_FILMS_DIRECTORS_FK FOREIGN KEY (DIRECTOR_ID)
           REFERENCES PUBLIC.DIRECTORS(DIRECTOR_ID)
           ON DELETE CASCADE ON UPDATE CASCADE
   );
   ```

## Примеры запросов
### 🎬 Раздел “Фильмы”
1. Все фильмы с рейтингом
    ```SQL
    SELECT f.name AS film_name,
           r.name AS rating_name,
           f.releaseDate,
           f.duration
    FROM film AS f
    JOIN rating AS r ON f.rating_id = r.rating_id;
    ```
2. Фильмы и их жанры
   ```SQL
   SELECT f.name AS film_name,
          g.name AS genre_name
   FROM film AS f
   JOIN films_geners AS fg ON f.film_id = fg.film_id
   JOIN genre AS g ON fg.genre_id = g.genre_id
   ORDER BY f.name;
   ```

3. Количество жанров у каждого фильма
   ```SQL
   SELECT f.name AS film_name,
   COUNT(fg.genre_id) AS genre_count
   FROM film AS f
   JOIN films_geners AS fg ON f.film_id = fg.film_id
   GROUP BY f.film_id, f.name
   ORDER BY genre_count DESC;
   ```

4. Топ фильмов по лайкам
   ```SQL
   SELECT f.name AS film_name,
   COUNT(fl.user_id) AS likes_count
   FROM film AS f
   JOIN film_likes AS fl ON f.film_id = fl.film_id
   GROUP BY f.film_id, f.name
   ORDER BY likes_count DESC
   LIMIT 10;
   ```

### 👤 Раздел “Пользователи”
5. Список друзей пользователя
   ```SQL
   SELECT u2.name AS friend_name,
          fs.name AS status,
          fs.isAccepted
   FROM friendships AS f
   JOIN user AS u1 ON f.user_id = u1.user_id
   JOIN user AS u2 ON f.friend_id = u2.user_id
   JOIN friendship_status AS fs ON f.friendship_status_id = fs.friendship_status_id
   WHERE u1.login = 'boss';
   ```

6. Количество друзей у каждого пользователя
   ```SQL
   SELECT u.name,
   COUNT(f.friend_id) AS total_friends
   FROM user AS u
   JOIN friendships AS f ON u.user_id = f.user_id
   JOIN friendship_status AS fs ON f.friendship_status_id = fs.friendship_status_id
   WHERE fs.isAccepted = TRUE
   GROUP BY u.user_id, u.name
   ORDER BY total_friends DESC;
   ```

7. Кто лайкнул больше всего фильмов
   ```SQL
   SELECT u.name,
   COUNT(fl.film_id) AS total_likes
   FROM user AS u
   JOIN film_likes AS fl ON u.user_id = fl.user_id
   GROUP BY u.user_id, u.name
   ORDER BY total_likes DESC
   LIMIT 10;
   ```
---

В полной мере реализован функционал нахождения общих фильмов, ленты событий и рекомендаций. Также реализованы функции удаления фильмов и пользователей, нахождения популярных фильмов и работа с отзывами. Еще выполнены задачи фильмы по режиссёрам и поиск.

**Исполнитель / Функциональность / Ветка задачи(SP) :**
- yarosimo1/Общие фильмы/add-common-films(1)
- yarosimo1/Лента событий/add-feed(3)
- yarosimo1/Рекомендации/add-recommendations(3)


- kumis-dev/Удаление фильмов и пользователей/add-remove-endpoint(2)
- kumis-dev/Популярные фильмы/add-most-populars(2)
- kumis-dev/Отзывы/add-reviews(4)


- ropgocTb/Фильмы по режиссёрам/add-director(4)
- ropgocTb/Поиск/add-search(3)