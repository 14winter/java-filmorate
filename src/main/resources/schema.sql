CREATE TABLE IF NOT EXISTS genres (
  genre_id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  user_id SERIAL PRIMARY KEY,
  email varchar(320) NOT NULL,
  login varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
  user_id int NOT NULL,
  friend_id int NOT NULL,
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (friend_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS movies (
  film_id SERIAL PRIMARY KEY,
  title varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  release_date date NOT NULL,
  duration int NOT NULL,
  mpa_id int NOT NULL REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id int NOT NULL REFERENCES movies(film_id),
  user_id int NOT NULL REFERENCES users(user_id),
  PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS movie_genres (
  film_id int NOT NULL REFERENCES movies(film_id),
  genre_id int NOT NULL REFERENCES genres(genre_id),
  PRIMARY KEY (film_id, genre_id)
);