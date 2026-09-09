package com.athlitrack.db;

import com.athlitrack.model.Exercise;
import com.athlitrack.model.ExerciseProgress;
import com.athlitrack.model.Workout;
import com.athlitrack.model.WorkoutExercise;
import com.athlitrack.model.WorkoutSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Operazioni di lettura e scrittura sui workout.
 * Le date vengono salvate come testo ISO (es. 2025-02-10T14:30:00).
 */
public class WorkoutDao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Workout> findAll() throws SQLException {
        List<Workout> workout = new ArrayList<>();
        String sql = "SELECT * FROM workouts ORDER BY start_time";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Workout w = rigaInWorkout(rs);
                caricaEsercizi(w);
                workout.add(w);
            }
        }
        return workout;
    }

    public Workout findById(int id) throws SQLException {
        String sql = "SELECT * FROM workouts WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Workout w = rigaInWorkout(rs);
                    caricaEsercizi(w);
                    return w;
                }
            }
        }
        return null;
    }

    public void insert(Workout w) throws SQLException {
        String sql = "INSERT INTO workouts (name, start_time, end_time) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, w.getName());
            ps.setString(2, w.getStart().format(FORMATO));
            ps.setString(3, w.getEnd().format(FORMATO));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    w.setId(rs.getInt(1));
                }
            }
        }
        inserisciEsercizi(w);
    }

    public void update(Workout w) throws SQLException {
        String sql = "UPDATE workouts SET name = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, w.getName());
            ps.setString(2, w.getStart().format(FORMATO));
            ps.setString(3, w.getEnd().format(FORMATO));
            ps.setInt(4, w.getId());
            ps.executeUpdate();
        }
        // Sostituisce la lista degli esercizi: elimina i vecchi (con i set) e li reinserisce.
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM workout_exercises WHERE workout_id = ?")) {
            ps.setInt(1, w.getId());
            ps.executeUpdate();
        }
        inserisciEsercizi(w);
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM workouts WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean esisteNome(String nome, int escludiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM workouts WHERE name = ? AND id <> ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, escludiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Controlla se il periodo [inizio, fine] si sovrappone a un altro workout.
     * Due intervalli si sovrappongono quando inizio < fineAltro E fine > inizioAltro.
     */
    public boolean esisteSovrapposizione(LocalDateTime inizio, LocalDateTime fine, int escludiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM workouts WHERE end_time > ? AND start_time < ? AND id <> ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, inizio.format(FORMATO));
            ps.setString(2, fine.format(FORMATO));
            ps.setInt(3, escludiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Restituisce tutte le esecuzioni di un esercizio: una riga per ogni workout
     * che lo contiene, con il volume totale (somma peso x reps).
     */
    public List<ExerciseProgress> trovaEsecuzioni(int exerciseId) throws SQLException {
        List<ExerciseProgress> esecuzioni = new ArrayList<>();
        String sql = "SELECT w.start_time, COALESCE(SUM(ws.weight * ws.reps), 0) AS volume "
                + "FROM workouts w "
                + "JOIN workout_exercises we ON we.workout_id = w.id "
                + "JOIN workout_sets ws ON ws.workout_exercise_id = we.id "
                + "WHERE we.exercise_id = ? "
                + "GROUP BY w.id ORDER BY w.start_time";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, exerciseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime data = LocalDateTime.parse(rs.getString("start_time"), FORMATO);
                    esecuzioni.add(new ExerciseProgress(data, rs.getDouble("volume")));
                }
            }
        }
        return esecuzioni;
    }

    private Workout rigaInWorkout(ResultSet rs) throws SQLException {
        return new Workout(
                rs.getInt("id"),
                rs.getString("name"),
                LocalDateTime.parse(rs.getString("start_time"), FORMATO),
                LocalDateTime.parse(rs.getString("end_time"), FORMATO));
    }

    /** Carica gli esercizi e i set di un workout ordinati per posizione. */
    private void caricaEsercizi(Workout w) throws SQLException {
        String sql = "SELECT we.id AS we_id, we.exercise_id, we.position, "
                + "e.name AS ex_name, e.description AS ex_desc, e.type AS ex_type "
                + "FROM workout_exercises we JOIN exercises e ON e.id = we.exercise_id "
                + "WHERE we.workout_id = ? ORDER BY we.position";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, w.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Exercise e = new Exercise(rs.getInt("exercise_id"), rs.getString("ex_name"),
                            rs.getString("ex_desc"), rs.getString("ex_type"));
                    WorkoutExercise we = new WorkoutExercise(e, rs.getInt("position"));
                    we.setId(rs.getInt("we_id"));
                    caricaSet(we);
                    w.getExercises().add(we);
                }
            }
        }
    }

    private void caricaSet(WorkoutExercise we) throws SQLException {
        String sql = "SELECT * FROM workout_sets WHERE workout_exercise_id = ? ORDER BY set_number";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, we.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkoutSet s = new WorkoutSet(rs.getInt("set_number"), rs.getDouble("weight"), rs.getInt("reps"));
                    s.setId(rs.getInt("id"));
                    we.getSets().add(s);
                }
            }
        }
    }

    /** Inserisce gli esercizi e i set di un workout rispettando l'ordine. */
    private void inserisciEsercizi(Workout w) throws SQLException {
        String sqlWe = "INSERT INTO workout_exercises (workout_id, exercise_id, position) VALUES (?, ?, ?)";
        String sqlWs = "INSERT INTO workout_sets (workout_exercise_id, set_number, weight, reps) VALUES (?, ?, ?, ?)";
        try (PreparedStatement psWe = Database.getConnection().prepareStatement(sqlWe, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psWs = Database.getConnection().prepareStatement(sqlWs)) {
            for (WorkoutExercise we : w.getExercises()) {
                psWe.setInt(1, w.getId());
                psWe.setInt(2, we.getExercise().getId());
                psWe.setInt(3, we.getPosition());
                psWe.executeUpdate();
                int weId;
                try (ResultSet rs = psWe.getGeneratedKeys()) {
                    rs.next();
                    weId = rs.getInt(1);
                }
                for (WorkoutSet s : we.getSets()) {
                    psWs.setInt(1, weId);
                    psWs.setInt(2, s.getSetNumber());
                    psWs.setDouble(3, s.getWeight());
                    psWs.setInt(4, s.getReps());
                    psWs.addBatch();
                }
                psWs.executeBatch();
            }
        }
    }
}