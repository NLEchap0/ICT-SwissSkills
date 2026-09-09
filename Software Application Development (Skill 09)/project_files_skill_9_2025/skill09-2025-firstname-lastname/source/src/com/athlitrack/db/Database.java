package com.athlitrack.db;

import com.athlitrack.model.Exercise;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce la connessione al database SQLite, la creazione dello schema
 * e l'importazione degli esercizi iniziali contenuti in exercises.json.
 *
 * Il file del database viene creato automaticamente nella cartella "database"
 * accanto all'eseguibile alla prima esecuzione.
 */
public class Database {

    private static final String URL = "jdbc:sqlite:database/athlitrack.db";
    private static Connection connessione;

    private Database() {
    }

    /** Restituisce la connessione unica al database, creandola se necessario. */
    public static Connection getConnection() throws SQLException {
        if (connessione == null || connessione.isClosed()) {
            new File("database").mkdirs();
            connessione = DriverManager.getConnection(URL);
            try (Statement st = connessione.createStatement()) {
                // Abilita le eliminazioni "a cascata" sulle chiavi esterne.
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connessione;
    }

    /**
     * Crea le tabelle (se non esistono) leggendo lo schema da schema.sql
     * e poi importa gli esercizi iniziali se la tabella e' vuota.
     */
    public static void inizializza() throws SQLException, IOException {
        eseguiSchema();
        importaEsercizi();
    }

    /** Esegue le istruzioni SQL contenute nel file schema.sql. */
    private static void eseguiSchema() throws SQLException, IOException {
        String sql = leggiRisorsa("/db/schema.sql");
        try (Statement st = getConnection().createStatement()) {
            for (String istruzione : sql.split(";")) {
                if (!istruzione.trim().isEmpty()) {
                    st.execute(istruzione);
                }
            }
        }
    }

    /** Importa gli esercizi da exercises.json solo se la tabella e' vuota. */
    private static void importaEsercizi() throws SQLException, IOException {
        if (conta("exercises") > 0) {
            return;
        }
        String json = leggiRisorsa("/exercises.json");
        ExerciseDao dao = new ExerciseDao();
        for (Exercise e : parseJson(json)) {
            dao.insert(e);
        }
    }

    /** Legge un file dalle risorse (all'interno del jar o della cartella classes). */
    private static String leggiRisorsa(String percorso) throws IOException {
        try (InputStream in = Database.class.getResourceAsStream(percorso)) {
            if (in == null) {
                throw new IOException("Risorsa non trovata: " + percorso);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static int conta(String tabella) throws SQLException {
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabella)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /**
     * Parser JSON molto semplice, pensato per il formato fisso di exercises.json:
     * una lista di oggetti con i campi name, description e type.
     * Assunto: nessun carattere '{', '}' o '"' dentro i valori dei campi.
     */
    public static List<Exercise> parseJson(String testo) {
        List<Exercise> result = new ArrayList<>();
        int pos = 0;
        while (true) {
            int inizio = testo.indexOf('{', pos);
            if (inizio < 0) {
                break;
            }
            int fine = testo.indexOf('}', inizio);
            String oggetto = testo.substring(inizio + 1, fine);
            String nome = campo(oggetto, "name");
            String descrizione = campo(oggetto, "description");
            String tipo = campo(oggetto, "type");
            result.add(new Exercise(0, nome, descrizione, tipo));
            pos = fine + 1;
        }
        return result;
    }

    /** Estrae il valore testuale di una chiave JSON da un singolo oggetto. */
    private static String campo(String oggetto, String chiave) {
        int indice = oggetto.indexOf("\"" + chiave + "\"");
        if (indice < 0) {
            return "";
        }
        int duePunti = oggetto.indexOf(':', indice);
        int virgolette1 = oggetto.indexOf('"', duePunti);
        int virgolette2 = oggetto.indexOf('"', virgolette1 + 1);
        return oggetto.substring(virgolette1 + 1, virgolette2);
    }
}