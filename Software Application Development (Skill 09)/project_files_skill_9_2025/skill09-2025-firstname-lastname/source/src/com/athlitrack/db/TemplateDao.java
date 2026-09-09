package com.athlitrack.db;

import com.athlitrack.model.Exercise;
import com.athlitrack.model.Template;
import com.athlitrack.model.TemplateItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operazioni di lettura e scrittura sui template di allenamento.
 * Ogni template viene sempre caricato con la propria lista ordinata di esercizi.
 */
public class TemplateDao {

    public List<Template> findAll() throws SQLException {
        List<Template> template = new ArrayList<>();
        String sql = "SELECT * FROM templates ORDER BY name";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Template t = new Template(rs.getInt("id"), rs.getString("name"));
                caricaItems(t);
                template.add(t);
            }
        }
        return template;
    }

    public Template findById(int id) throws SQLException {
        String sql = "SELECT * FROM templates WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Template t = new Template(rs.getInt("id"), rs.getString("name"));
                    caricaItems(t);
                    return t;
                }
            }
        }
        return null;
    }

    public void insert(Template t) throws SQLException {
        String sql = "INSERT INTO templates (name) VALUES (?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
            }
        }
        inserisciItems(t);
    }

    public void update(Template t) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE templates SET name = ? WHERE id = ?")) {
            ps.setString(1, t.getName());
            ps.setInt(2, t.getId());
            ps.executeUpdate();
        }
        // Sostituisce la lista degli esercizi: elimina i vecchi e li reinserisce.
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM template_items WHERE template_id = ?")) {
            ps.setInt(1, t.getId());
            ps.executeUpdate();
        }
        inserisciItems(t);
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM templates WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean esisteNome(String nome, int escludiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM templates WHERE name = ? AND id <> ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, escludiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** Carica gli esercizi di un template ordinati per posizione. */
    private void caricaItems(Template t) throws SQLException {
        String sql = "SELECT ti.*, e.name AS ex_name, e.description AS ex_desc, e.type AS ex_type "
                + "FROM template_items ti JOIN exercises e ON e.id = ti.exercise_id "
                + "WHERE ti.template_id = ? ORDER BY ti.position";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, t.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Exercise e = new Exercise(rs.getInt("exercise_id"), rs.getString("ex_name"),
                            rs.getString("ex_desc"), rs.getString("ex_type"));
                    TemplateItem item = new TemplateItem(e, rs.getInt("position"), rs.getInt("sets"), rs.getInt("reps"));
                    item.setId(rs.getInt("id"));
                    t.getItems().add(item);
                }
            }
        }
    }

    /** Inserisce gli esercizi di un template rispettando l'ordine. */
    private void inserisciItems(Template t) throws SQLException {
        String sql = "INSERT INTO template_items (template_id, exercise_id, position, sets, reps) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            for (TemplateItem item : t.getItems()) {
                ps.setInt(1, t.getId());
                ps.setInt(2, item.getExercise().getId());
                ps.setInt(3, item.getPosition());
                ps.setInt(4, item.getSets());
                ps.setInt(5, item.getReps());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}