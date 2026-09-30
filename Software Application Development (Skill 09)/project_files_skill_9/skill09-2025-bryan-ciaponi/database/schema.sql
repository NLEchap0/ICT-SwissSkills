-- AthliTrack database schema (SQLite).
-- The application creates these tables automatically on first start
-- (see source/db.py) and imports exercises.json when the tables are empty,
-- so no manual setup is needed. This file is provided for documentation
-- and for the evaluation of the database structure.

CREATE TABLE IF NOT EXISTS exercises (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL,
    type        TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS templates (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS template_exercises (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    template_id INTEGER NOT NULL REFERENCES templates(id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
    position    INTEGER NOT NULL,
    sets        INTEGER NOT NULL,
    reps        INTEGER NOT NULL,
    UNIQUE (template_id, exercise_id)
);

CREATE TABLE IF NOT EXISTS workouts (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT NOT NULL UNIQUE,
    start TEXT NOT NULL,   -- stored as 'YYYY-MM-DD HH:MM'
    end   TEXT NOT NULL    -- stored as 'YYYY-MM-DD HH:MM'
);

CREATE TABLE IF NOT EXISTS workout_exercises (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    workout_id  INTEGER NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
    position    INTEGER NOT NULL,
    UNIQUE (workout_id, exercise_id)
);

CREATE TABLE IF NOT EXISTS sets (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    workout_exercise_id INTEGER NOT NULL REFERENCES workout_exercises(id) ON DELETE CASCADE,
    set_number          INTEGER NOT NULL,
    weight              REAL NOT NULL,
    reps                INTEGER NOT NULL
);
