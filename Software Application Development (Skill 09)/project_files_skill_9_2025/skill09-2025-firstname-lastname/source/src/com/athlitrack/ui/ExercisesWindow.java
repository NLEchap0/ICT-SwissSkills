package com.athlitrack.ui;

import com.athlitrack.db.ExerciseDao;
import com.athlitrack.model.Exercise;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Finestra degli esercizi: elenco con nome, descrizione e tipo.
 * Permette di aggiungere, modificare, eliminare gli esercizi e di aprire la finestra Progress.
 */
public class ExercisesWindow extends Stage {

    private final ExerciseDao dao = new ExerciseDao();
    private final TableView<Exercise> tabella = new TableView<>();
    private final ObservableList<Exercise> dati = FXCollections.observableArrayList();

    public ExercisesWindow() {
        setTitle("AthliTrack - Exercises");
        BorderPane root = new BorderPane();
        root.setTop(new Label("Esercizi disponibili"));
        root.setCenter(tabella);
        root.setBottom(creaBarraBottoni());

        creaColonne();
        tabella.setItems(dati);
        tabella.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tabella.setPlaceholder(new Label("Nessun esercizio presente."));

        Scene scena = new Scene(root, 800, 500);
        setScene(scena);
        setMinWidth(600);
        setMinHeight(400);
        ricarica();
    }

    private ToolBar creaBarraBottoni() {
        javafx.scene.control.Button aggiungi = UiUtil.bottone("Add Exercise", e -> apriDialogo(null));
        javafx.scene.control.Button modifica = UiUtil.bottone("Edit Exercise", e -> apriDialogo(selezionato()));
        javafx.scene.control.Button elimina = UiUtil.bottone("Delete Exercise", e -> eliminaSelezionato());
        javafx.scene.control.Button progress = UiUtil.bottone("My Progress", e -> apriProgress());
        return new ToolBar(aggiungi, modifica, elimina, progress);
    }

    private void creaColonne() {
        TableColumn<Exercise, String> nome = new TableColumn<>("Name");
        nome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        nome.setPrefWidth(180);

        TableColumn<Exercise, String> descrizione = new TableColumn<>("Description");
        descrizione.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        descrizione.setPrefWidth(420);

        TableColumn<Exercise, String> tipo = new TableColumn<>("Type");
        tipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        tipo.setPrefWidth(120);

        tabella.getColumns().addAll(nome, descrizione, tipo);
    }

    private Exercise selezionato() {
        return tabella.getSelectionModel().getSelectedItem();
    }

    private void apriDialogo(Exercise esercizio) {
        ExerciseDialog dialogo = new ExerciseDialog(esercizio, this::ricarica, this);
        dialogo.showAndWait();
    }

    private void apriProgress() {
        Exercise e = selezionato();
        if (e == null) {
            UiUtil.errore("Selezionare prima un esercizio dalla tabella.");
            return;
        }
        new ProgressWindow(e).show();
    }

    private void eliminaSelezionato() {
        Exercise e = selezionato();
        if (e == null) {
            UiUtil.errore("Selezionare prima un esercizio dalla tabella.");
            return;
        }
        if (UiUtil.conferma("Eliminare l'esercizio '" + e.getName() + "'?")) {
            try {
                dao.delete(e.getId());
                ricarica();
            } catch (Exception errore) {
                UiUtil.errore("Eliminazione non riuscita: " + errore.getMessage());
            }
        }
    }

    /** Ricarica i dati dal database. */
    private void ricarica() {
        try {
            dati.setAll(dao.findAll());
        } catch (Exception e) {
            UiUtil.errore("Lettura degli esercizi non riuscita: " + e.getMessage());
        }
    }
}