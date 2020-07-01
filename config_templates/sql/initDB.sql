DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS cities;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS group_type;
DROP TYPE IF EXISTS user_flag;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');

CREATE TYPE group_type AS ENUM ('FINISHED', 'CURRENT', 'REGISTERING');

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE cities
(
    id   TEXT PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE groups
(
    id   TEXT PRIMARY KEY,
    type group_type NOT NULL
);

CREATE TABLE users
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
    full_name TEXT      NOT NULL,
    email     TEXT      NOT NULL,
    flag      user_flag NOT NULL
);
CREATE UNIQUE INDEX email_idx ON users (email);

CREATE TABLE projects
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
    name        TEXT NOT NULL,
    description TEXT NOT NULL
);