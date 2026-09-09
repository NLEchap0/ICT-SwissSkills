-- Schema del database AthliTrack (SQLite).
-- Le istruzioni vengono eseguite all'avvio dell'applicazione.

CREATE TABLE IF NOT EXISTS exercises (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL UNIQUE,
    description TEXT    NOT NULL,
    type        TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS templates (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS template_items (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    template_id INTEGER NOT NULL REFERENCES templates (id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL REFERENCES exercises (id) ON DELETE CASCADE,
    position    INTEGER NOT NULL,
    sets        INTEGER NOT NULL,
    reps        INTEGER NOT NULL,
    UNIQUE (template_id, exercise_id)
);

CREATE TABLE IF NOT EXISTS workouts (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT    NOT NULL UNIQUE,
    start_time TEXT    NOT NULL,
    end_time   TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS workout_exercises (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    workout_id  INTEGER NOT NULL REFERENCES workouts (id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL REFERENCES exercises (id) ON DELETE CASCADE,
    position    INTEGER NOT NULL,
    UNIQUE (workout_id, exercise_id)
);

CREATE TABLE IF NOT EXISTS workout_sets (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    workout_exercise_id INTEGER NOT NULL REFERENCES workout_exercises (id) ON DELETE CASCADE,
    set_number          INTEGER NOT NULL,
    weight              REAL    NOT NULL,
    reps                INTEGER NOT NULL
);