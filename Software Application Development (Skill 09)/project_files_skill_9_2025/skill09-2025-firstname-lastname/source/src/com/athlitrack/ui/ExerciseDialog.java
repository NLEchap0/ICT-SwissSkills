package com.athlitrack.ui;

import com.athlitrack.db.ExerciseDao;
import com.athlitrack.model.Exercise;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Finestra modale per aggiungere o modificare un esercizio.
 * Le modifiche vengono salvate solo premendo "Save".
 */
public class ExerciseDialog extends Stage {

    private final ExerciseDao dao = new ExerciseDao();
    private final Exercise esercizio;
    private final Runnable onSaved;

    private final TextField nome = new TextField();
    private final TextField descrizione = new TextField();
    private final ComboBox<String> tipo = new ComboBox<>();
    private final Label erroreNome = UiUtil.etichettaErrore();
    private final Label erroreDescrizione = UiUtil.etichettaErrore();
    private final Label erroreTipo = UiUtil.etichettaErrore();

    public ExerciseDialog(Exercise esercizio, Runnable onSaved, Window owner) {
        this.esercizio = esercizio;
        this.onSaved = onSaved;

        setTitle(esercizio == null ? "Aggiungi esercizio" : "Modifica esercizio");
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        // Tipi di attrezzatura conosciuti; la ComboBox resta modificabile per tipi custom.
        tipo.getItems().addAll("barbell", "dumbbell", "cable", "machine");
        tipo.setEditable(true);

        GridPane griglia = new GridPane();
        griglia.setHgap(10);
        griglia.setVgap(10);
        griglia.setPadding(new Insets(15));
        griglia.addRow(0, new Label("Name*"), nome);
        griglia.add(erroreNome, 1, 1);
        griglia.addRow(2, new Label("Description*"), descrizione);
        griglia.add(erroreDescrizione, 1, 3);
        griglia.addRow(4, new Label("Type*"), tipo);
        griglia.add(erroreTipo, 1, 5);

        javafx.scene.control.Button salva = UiUtil.bottone("Save", e -> salva());
        javafx.scene.control.Button annulla = UiUtil.bottone("Cancel", e -> chiudi(false));
        HBox bottoni = new HBox(10, salva, annulla);

        VBox root = new VBox(10, griglia, bottoni);
        root.setPadding(new Insets(10));
        setScene(new Scene(root, 480, 280));
        setMinWidth(460);
        setMinHeight(260);

        // Al tentativo di chiusura, se ci sono modifiche non salvate chiede conferma.
        setOnCloseRequest(e -> {
            if (e.isConsumed()) {
                return;
            }
            if (modificheNonSalvate() && !UiUtil.conferma("Sono presenti modifiche non salvate. Annullare e perdere le modifiche?")) {
                e.consume();
            }
        });

        precompila();
    }

    /** Se si sta modificando, mostra i valori attuali dell'esercizio. */
    private void precompila() {
        if (esercizio != null) {
            nome.setText(esercizio.getName());
            descrizione.setText(esercizio.getDescription());
            tipo.setValue(esercizio.getType());
        }
    }

    /** True se i campi sono stati modificati rispetto allo stato iniziale. */
    private boolean modificheNonSalvate() {
        String stato = (nome.getText() + "|" + descrizione.getText() + "|" + tipo.getValue());
        if (esercizio == null) {
            return !stato.equals("||") && !stato.equals("|") && !stato.isEmpty();
        }
        return !stato.equals(esercizio.getName() + "|" + esercizio.getDescription() + "|" + esercizio.getType());
    }

    /** Valida i campi, salva e chiude la finestra. */
    private void salva() {
        String nomeTesto = nome.getText() == null ? "" : nome.getText().trim();
        String descrizioneTesto = descrizione.getText() == null ? "" : descrizione.getText().trim();
        String tipoTesto = tipo.getValue() == null ? "" : tipo.getValue().trim();

        boolean valido = true;
        UiUtil.erroreCampo(erroreNome, null);
        UiUtil.erroreCampo(erroreDescrizione, null);
        UiUtil.erroreCampo(erroreTipo, null);

        if (nomeTesto.isEmpty()) {
            UiUtil.erroreCampo(erroreNome, "Il nome e' obbligatorio.");
            valido = false;
        }
        if (descrizioneTesto.isEmpty()) {
            UiUtil.erroreCampo(erroreDescrizione, "La descrizione e' obbligatoria.");
            valido = false;
        }
        if (tipoTesto.isEmpty()) {
            UiUtil.erroreCampo(erroreTipo, "Il tipo e' obbligatorio.");
            valido = false;
        }
        if (valido) {
            try {
                int escludiId = esercizio == null ? 0 : esercizio.getId();
                if (dao.esisteNome(nomeTesto, escludiId)) {
                    UiUtil.erroreCampo(erroreNome, "Esiste gia' un esercizio con questo nome.");
                    valido = false;
                }
            } catch (Exception e) {
                UiUtil.errore("Controllo del nome non riuscito: " + e.getMessage());
                return;
            }
        }
        if (!valido) {
            return;
        }

        try {
            if (esercizio == null) {
                dao.insert(new Exercise(0, nomeTesto, descrizioneTesto, tipoTesto));
            } else {
                esercizio.setName(nomeTesto);
                esercizio.setDescription(descrizioneTesto);
                esercizio.setType(tipoTesto);
                dao.update(esercizio);
            }
            onSaved.run();
            chiudi(true);
        } catch (Exception e) {
            UiUtil.errore("Salvataggio non riuscito: " + e.getMessage());
        }
    }

    private void chiudi(boolean salvato) {
        close();
    }
}