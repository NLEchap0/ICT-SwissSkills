package athlitrack;

import athlitrack.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Livello dati SQLite. Tutto l'SQL sta qui dentro; la UI chiama solo questi metodi.
 * Il file del database viene creato accanto al programma (athlitrack.db),
 * così l'app è portabile: estrai, doppio click, via.
 */
public class Database {

    /** Formato di salvataggio delle date/ore: "2025-03-10 18:30". */
    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Connection con;

    /** Apre (o crea) il file SQLite, attiva le foreign key e crea le tabelle. */
    public Database(File dbFile) {
        try {
            con = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement st = con.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            createSchema();
        } catch (SQLException e) {
            throw new DbException("Could not open database: " + e.getMessage(), e);
        }
    }

    /** Importa exercises.json al primo avvio (solo se la tabella è vuota). */
    public void importExercisesIfEmpty(InputStream json) {
        try {
            if (count("exercises") > 0) {
                return;
            }
            List<Map<String, String>> items = new Gson().fromJson(
                    new InputStreamReader(json, StandardCharsets.UTF_8),
                    new TypeToken<List<Map<String, String>>>() { }.getType());
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT OR IGNORE INTO exercises (name, description, type) VALUES (?,?,?)")) {
                for (Map<String, String> it : items) {
                    String name = it.getOrDefault("name", "").trim();
                    if (name.isEmpty()) {
                        continue;
                    }
                    ps.setString(1, name);
                    ps.setString(2, it.getOrDefault("description", "").trim());
                    ps.setString(3, it.getOrDefault("type", "").trim());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new DbException("Could not import exercises: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------ schema ---
    private void createSchema() throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS exercises ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL UNIQUE,"
                    + "description TEXT NOT NULL,"
                    + "type TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS templates ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL UNIQUE)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS template_exercises ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "template_id INTEGER NOT NULL REFERENCES templates(id) ON DELETE CASCADE,"
                    + "exercise_id INTEGER NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,"
                    + "position INTEGER NOT NULL,"
                    + "sets INTEGER NOT NULL,"
                    + "reps INTEGER NOT NULL,"
                    + "UNIQUE (template_id, exercise_id))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS workouts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL UNIQUE,"
                    + "start TEXT NOT NULL,"
                    + "end TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS workout_exercises ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "workout_id INTEGER NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,"
                    + "exercise_id INTEGER NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,"
                    + "position INTEGER NOT NULL,"
                    + "UNIQUE (workout_id, exercise_id))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS sets ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "workout_exercise_id INTEGER NOT NULL"
                    + " REFERENCES workout_exercises(id) ON DELETE CASCADE,"
                    + "set_number INTEGER NOT NULL,"
                    + "weight REAL NOT NULL,"
                    + "reps INTEGER NOT NULL)");
        }
    }

    private int count(String table) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.getInt(1);
        }
    }

    // ---------------------------------------------------------- esercizi --
    /** Tutti gli esercizi ordinati per nome (per liste e tabelle). */
    public List<Exercise> getExercises() {
        String sql = "SELECT * FROM exercises ORDER BY name";
        List<Exercise> out = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new Exercise(rs.getInt("id"), rs.getString("name"),
                        rs.getString("description"), rs.getString("type")));
            }
            return out;
        } catch (SQLException e) {
            throw new DbException("Could not load exercises.", e);
        }
    }

    /** Un singolo esercizio cercato per id (per le viste dettaglio/modifica). */
    public Exercise getExercise(int id) {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM exercises WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Exercise(rs.getInt("id"), rs.getString("name"),
                            rs.getString("description"), rs.getString("type"));
                }
                throw new DbException("Exercise not found.");
            }
        } catch (SQLException e) {
            throw new DbException("Could not load exercise.", e);
        }
    }

    /** Vero se il nome è già usato da un'altra riga (per messaggi d'errore chiari). */
    public boolean exerciseNameTaken(String name, Integer excludeId) {
        String sql = excludeId == null
                ? "SELECT id FROM exercises WHERE name=?"
                : "SELECT id FROM exercises WHERE name=? AND id<>?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DbException("Database error.", e);
        }
    }

    /** Inserisce un nuovo esercizio; rifiuta i nomi duplicati con un messaggio chiaro. */
    public void addExercise(String name, String description, String type) {
        if (exerciseNameTaken(name, null)) {
            throw new DbException("An exercise named '" + name + "' already exists.");
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO exercises (name, description, type) VALUES (?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Could not save exercise.", e);
        }
    }

    /** Aggiorna un esercizio esistente (stesso controllo sui duplicati). */
    public void updateExercise(int id, String name, String description, String type) {
        if (exerciseNameTaken(name, id)) {
            throw new DbException("An exercise named '" + name + "' already exists.");
        }
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE exercises SET name=?, description=?, type=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Could not save exercise.", e);
        }
    }

    /** Vero se l'esercizio è usato in un template o workout (non cancellabile). */
    public boolean exerciseInUse(int exerciseId) {
        try (PreparedStatement a = con.prepareStatement(
                     "SELECT COUNT(*) FROM template_exercises WHERE exercise_id=?");
             PreparedStatement b = con.prepareStatement(
                     "SELECT COUNT(*) FROM workout_exercises WHERE exercise_id=?")) {
            a.setInt(1, exerciseId);
            b.setInt(1, exerciseId);
            try (ResultSet ra = a.executeQuery(); ResultSet rb = b.executeQuery()) {
                return ra.getInt(1) + rb.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DbException("Database error.", e);
        }
    }

    public void deleteExercise(int id) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM exercises WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Could not delete exercise.", e);
        }
    }

    // ---------------------------------------------------------- template --
    /** Tutti i template ordinati per nome. */
    public List<Template> getTemplates() {
        List<Template> out = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM templates ORDER BY name")) {
            while (rs.next()) {
                out.add(new Template(rs.getInt("id"), rs.getString("name")));
            }
            return out;
        } catch (SQLException e) {
            throw new DbException("Could not load templates.", e);
        }
    }

    /** Un template con le sue righe esercizio, nell'ordine salvato. */
    public Template getTemplate(int id) {
        Template tpl;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM templates WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DbException("Template not found.");
                }
                tpl = new Template(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new DbException("Could not load template.", e);
        }
        String sql = "SELECT te.*, e.name AS ename FROM template_exercises te"
                + " JOIN exercises e ON e.id = te.exercise_id"
                + " WHERE te.template_id=? ORDER BY te.position";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tpl.getItems().add(new TemplateItem(rs.getInt("exercise_id"),
                            rs.getString("ename"), rs.getInt("sets"), rs.getInt("reps")));
                }
            }
            return tpl;
        } catch (SQLException e) {
            throw new DbException("Could not load template.", e);
        }
    }

    public boolean templateNameTaken(String name, Integer excludeId) {
        String sql = excludeId == null
                ? "SELECT id FROM templates WHERE name=?"
                : "SELECT id FROM templates WHERE name=? AND id<>?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DbException("Database error.", e);
        }
    }

    /** Crea (id null) o sostituisce un template con le sue righe ordinate. */
    public void saveTemplate(Integer id, String name, List<TemplateItem> items) {
        if (templateNameTaken(name, id)) {
            throw new DbException("A template named '" + name + "' already exists.");
        }
        try {
            con.setAutoCommit(false);
            int templateId;
            if (id == null) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO templates (name) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, name);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        templateId = keys.getInt(1);
                    }
                }
            } else {
                templateId = id;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE templates SET name=? WHERE id=?")) {
                    ps.setString(1, name);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM template_exercises WHERE template_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO template_exercises"
                            + " (template_id, exercise_id, position, sets, reps)"
                            + " VALUES (?,?,?,?,?)")) {
                for (int pos = 0; pos < items.size(); pos++) {
                    TemplateItem it = items.get(pos);
                    ps.setInt(1, templateId);
                    ps.setInt(2, it.getExerciseId());
                    ps.setInt(3, pos);
                    ps.setInt(4, it.getSets());
                    ps.setInt(5, it.getReps());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            con.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            throw new DbException("Could not save template.", e);
        } finally {
            autoCommitQuietly(true);
        }
    }

    public void deleteTemplate(int id) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM templates WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Could not delete template.", e);
        }
    }

    // ----------------------------------------------------------- workout --
    /** Tutti i workout ordinati per data di inizio (per la finestra principale). */
    public List<WorkoutSummary> getWorkouts() {
        List<WorkoutSummary> out = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM workouts ORDER BY start")) {
            while (rs.next()) {
                out.add(new WorkoutSummary(rs.getInt("id"), rs.getString("name"),
                        LocalDateTime.parse(rs.getString("start"), FMT),
                        LocalDateTime.parse(rs.getString("end"), FMT)));
            }
            return out;
        } catch (SQLException e) {
            throw new DbException("Could not load workouts.", e);
        }
    }

    /** Un workout completo di esercizi e serie, in ordine. */
    public Workout getWorkout(int id) {
        Workout w;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM workouts WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DbException("Workout not found.");
                }
                w = new Workout(rs.getInt("id"), rs.getString("name"),
                        LocalDateTime.parse(rs.getString("start"), FMT),
                        LocalDateTime.parse(rs.getString("end"), FMT));
            }
        } catch (SQLException e) {
            throw new DbException("Could not load workout.", e);
        }
        String sql = "SELECT we.id AS weid, we.exercise_id, e.name AS ename"
                + " FROM workout_exercises we JOIN exercises e ON e.id = we.exercise_id"
                + " WHERE we.workout_id=? ORDER BY we.position";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkoutExercise we = new WorkoutExercise(
                            rs.getInt("exercise_id"), rs.getString("ename"));
                    int weId = rs.getInt("weid");
                    try (PreparedStatement ps2 = con.prepareStatement(
                            "SELECT * FROM sets WHERE workout_exercise_id=? ORDER BY set_number")) {
                        ps2.setInt(1, weId);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                we.getSets().add(new SetEntry(
                                        rs2.getDouble("weight"), rs2.getInt("reps")));
                            }
                        }
                    }
                    w.getExercises().add(we);
                }
            }
            return w;
        } catch (SQLException e) {
            throw new DbException("Could not load workout.", e);
        }
    }

    public boolean workoutNameTaken(String name, Integer excludeId) {
        String sql = excludeId == null
                ? "SELECT id FROM workouts WHERE name=?"
                : "SELECT id FROM workouts WHERE name=? AND id<>?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DbException("Database error.", e);
        }
    }

    /** Primo workout che si sovrappone a [start, end), oppure null se libero. */
    public WorkoutSummary findOverlap(LocalDateTime start, LocalDateTime end, Integer excludeId) {
        for (WorkoutSummary w : getWorkouts()) {
            if (excludeId != null && w.getId() == excludeId) {
                continue;
            }
            if (start.isBefore(w.getEnd()) && end.isAfter(w.getStart())) {
                return w;
            }
        }
        return null;
    }

    /** Crea (id null) o sostituisce un workout; le serie vengono numerate 1..n. */
    public void saveWorkout(Integer id, String name, LocalDateTime start, LocalDateTime end,
                            List<WorkoutExercise> items) {
        if (workoutNameTaken(name, id)) {
            throw new DbException("A workout named '" + name + "' already exists.");
        }
        try {
            con.setAutoCommit(false);
            int workoutId;
            if (id == null) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO workouts (name, start, end) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, name);
                    ps.setString(2, start.format(FMT));
                    ps.setString(3, end.format(FMT));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        workoutId = keys.getInt(1);
                    }
                }
            } else {
                workoutId = id;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE workouts SET name=?, start=?, end=? WHERE id=?")) {
                    ps.setString(1, name);
                    ps.setString(2, start.format(FMT));
                    ps.setString(3, end.format(FMT));
                    ps.setInt(4, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM workout_exercises WHERE workout_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            for (int pos = 0; pos < items.size(); pos++) {
                WorkoutExercise we = items.get(pos);
                int weId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO workout_exercises (workout_id, exercise_id, position)"
                                + " VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, workoutId);
                    ps.setInt(2, we.getExerciseId());
                    ps.setInt(3, pos);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        weId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO sets (workout_exercise_id, set_number, weight, reps)"
                                + " VALUES (?,?,?,?)")) {
                    for (int i = 0; i < we.getSets().size(); i++) {
                        SetEntry s = we.getSets().get(i);
                        ps.setInt(1, weId);
                        ps.setInt(2, i + 1); // numerazione automatica 1..n (spec)
                        ps.setDouble(3, s.getWeight());
                        ps.setInt(4, s.getReps());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            con.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            throw new DbException("Could not save workout.", e);
        } finally {
            autoCommitQuietly(true);
        }
    }

    public void deleteWorkout(int id) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM workouts WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Could not delete workout.", e);
        }
    }

    // ----------------------------------------------------------- progress --
    /** Tutte le esecuzioni di un esercizio, in ordine di tempo (sessioni dello stesso giorno separate). */
    public List<HistoryEntry> exerciseHistory(int exerciseId) {
        List<HistoryEntry> out = new ArrayList<>();
        String sql = "SELECT we.id AS weid, w.id AS wid, w.name AS wname, w.start AS wdate"
                + " FROM workout_exercises we JOIN workouts w ON w.id = we.workout_id"
                + " WHERE we.exercise_id=? ORDER BY w.start, w.id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, exerciseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int weId = rs.getInt("weid");
                    try (PreparedStatement ps2 = con.prepareStatement(
                            "SELECT COUNT(*) AS n, COALESCE(SUM(reps),0) AS r,"
                                    + " COALESCE(SUM(weight*reps),0) AS v"
                                    + " FROM sets WHERE workout_exercise_id=?")) {
                        ps2.setInt(1, weId);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            out.add(new HistoryEntry(rs.getInt("wid"), rs.getString("wname"),
                                    LocalDateTime.parse(rs.getString("wdate"), FMT),
                                    rs2.getDouble("v"), rs2.getInt("n"), rs2.getInt("r")));
                        }
                    }
                }
            }
            return out;
        } catch (SQLException e) {
            throw new DbException("Could not load progress.", e);
        }
    }

    /**
     * Prossimo volume atteso (formula della spec): aumento medio + ultimo volume.
     * Storico vuoto -> NaN; un solo record -> quel volume (nessun trend ancora).
     */
    public static double expectedNextVolume(List<HistoryEntry> history) {
        if (history.isEmpty()) {
            return Double.NaN;
        }
        if (history.size() == 1) {
            return history.get(0).getVolume();
        }
        double sum = 0;
        for (int i = 1; i < history.size(); i++) {
            sum += history.get(i).getVolume() - history.get(i - 1).getVolume();
        }
        return history.get(history.size() - 1).getVolume() + sum / (history.size() - 1);
    }

    /** Totali per la finestra di dettaglio: {volume, n. serie, n. ripetizioni}. */
    public static double[] workoutStats(Workout w) {
        double volume = 0;
        int sets = 0, reps = 0;
        for (WorkoutExercise we : w.getExercises()) {
            for (SetEntry s : we.getSets()) {
                volume += s.volume();
                sets++;
                reps += s.getReps();
            }
        }
        return new double[]{volume, sets, reps};
    }

    // ------------------------------------------------------------ aiuti -----
    private void rollbackQuietly() {
        try {
            con.rollback();
        } catch (SQLException ignored) {
            // Niente da fare di sensato: l'errore vero è già stato segnalato.
        }
    }

    private void autoCommitQuietly(boolean value) {
        try {
            con.setAutoCommit(value);
        } catch (SQLException ignored) {
            // Mantiene l'errore originale, se ce n'è uno.
        }
    }
}
