package com.athlitrack.ui;

import com.athlitrack.db.WorkoutDao;
import com.athlitrack.model.Workout;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Finestra principale: mostra l'elenco dei workout.
 * Da qui si naviga verso gli esercizi, i template e le finestre di modifica dei workout.
 */
public class MainWindow extends Stage {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final WorkoutDao dao = new WorkoutDao();
    private final TableView<Workout> tabella = new TableView<>();
    private final ObservableList<Workout> dati = FXCollections.observableArrayList();

    public MainWindow() {
        setTitle("AthliTrack - Workout");
        BorderPane root = new BorderPane();
        root.setTop(creaMenu());
        root.setCenter(tabella);
        root.setBottom(creaBarraBottoni());

        creaColonne();
        tabella.setItems(dati);
        tabella.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tabella.setPlaceholder(new javafx.scene.control.Label("Nessun workout presente."));

        Scene scena = new Scene(root, 800, 520);
        setScene(scena);
        setMinWidth(640);
        setMinHeight(420);
        ricarica();
    }

    /** Costruisce la barra dei menu per navigare verso le altre finestre. */
    private MenuBar creaMenu() {
        Menu menu = new Menu("Navigazione");
        MenuItem esercizi = new MenuItem("Finestra Esercizi");
        esercizi.setOnAction(e -> new ExercisesWindow().show());
        MenuItem template = new MenuItem("Finestra Template");
        template.setOnAction(e -> new TemplatesWindow().show());
        menu.getItems().addAll(esercizi, template);
        return new MenuBar(menu);
    }

    private ToolBar creaBarraBottoni() {
        javafx.scene.control.Button aggiungi = UiUtil.bottone("Add Workout", e -> apriEditor(null));
        javafx.scene.control.Button modifica = UiUtil.bottone("Edit Workout", e -> apriEditor(selezionato()));
        javafx.scene.control.Button visualizza = UiUtil.bottone("View Workout", e -> apriVisualizzazione());
        javafx.scene.control.Button elimina = UiUtil.bottone("Delete Workout", e -> eliminaSelezionato());
        return new ToolBar(aggiungi, modifica, visualizza, elimina);
    }

    private void creaColonne() {
        TableColumn<Workout, String> nome = new TableColumn<>("Name");
        nome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        nome.setPrefWidth(180);

        TableColumn<Workout, String> inizio = new TableColumn<>("Start");
        inizio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStart().format(FORMATO_DATA)));
        inizio.setPrefWidth(140);

        TableColumn<Workout, String> fine = new TableColumn<>("End");
        fine.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnd().format(FORMATO_DATA)));
        fine.setPrefWidth(140);

        TableColumn<Workout, String> durata = new TableColumn<>("Duration");
        durata.setCellValueFactory(c -> {
            Workout w = c.getValue();
            Duration d = Duration.between(w.getStart(), w.getEnd());
            long ore = d.toHours();
            long minuti = d.toMinutesPart();
            return new SimpleStringProperty(ore + " h " + minuti + " min");
        });
        durata.setPrefWidth(120);

        tabella.getColumns().addAll(nome, inizio, fine, durata);
    }

    private Workout selezionato() {
        return tabella.getSelectionModel().getSelectedItem();
    }

    private void apriEditor(Workout workout) {
        new WorkoutEditorWindow(workout, this::ricarica).show();
    }

    private void apriVisualizzazione() {
        Workout w = selezionato();
        if (w == null) {
            UiUtil.errore("Selezionare prima un workout dalla tabella.");
            return;
        }
        new ViewWorkoutWindow(w).show();
    }

    private void eliminaSelezionato() {
        Workout w = selezionato();
        if (w == null) {
            UiUtil.errore("Selezionare prima un workout dalla tabella.");
            return;
        }
        if (UiUtil.conferma("Eliminare il workout '" + w.getName() + "'?")) {
            try {
                dao.delete(w.getId());
                ricarica();
            } catch (Exception e) {
                UiUtil.errore("Eliminazione non riuscita: " + e.getMessage());
            }
        }
    }

    /** Ricarica i dati dalla tabella del database. */
    private void ricarica() {
        try {
            dati.setAll(dao.findAll());
        } catch (Exception e) {
            UiUtil.errore("Lettura dei workout non riuscita: " + e.getMessage());
        }
    }
}