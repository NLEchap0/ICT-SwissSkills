package com.athlitrack.db;

import com.athlitrack.model.Exercise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operazioni di lettura e scrittura sugli esercizi.
 */
public class ExerciseDao {

    public List<Exercise> findAll() throws SQLException {
        List<Exercise> esercizi = new ArrayList<>();
        String sql = "SELECT * FROM exercises ORDER BY name";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                esercizi.add(rigaInEsercizio(rs));
            }
        }
        return esercizi;
    }

    public Exercise findById(int id) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rigaInEsercizio(rs);
                }
            }
        }
        return null;
    }

    public void insert(Exercise e) throws SQLException {
        String sql = "INSERT INTO exercises (name, description, type) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getType());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(Exercise e) throws SQLException {
        String sql = "UPDATE exercises SET name = ?, description = ?, type = ? WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getType());
            ps.setInt(4, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM exercises WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Controlla se il nome e' gia' usato da un altro esercizio.
     * Se "escludiId" e' maggiore di 0, quel record viene ignorato (usato in modifica).
     */
    public boolean esisteNome(String nome, int escludiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM exercises WHERE name = ? AND id <> ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, escludiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private Exercise rigaInEsercizio(ResultSet rs) throws SQLException {
        return new Exercise(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("type"));
    }
}