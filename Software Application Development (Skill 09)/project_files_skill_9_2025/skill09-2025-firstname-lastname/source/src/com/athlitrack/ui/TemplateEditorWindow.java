package com.athlitrack.ui;

import com.athlitrack.db.ExerciseDao;
import com.athlitrack.db.TemplateDao;
import com.athlitrack.model.Exercise;
import com.athlitrack.model.Template;
import com.athlitrack.model.TemplateItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Finestra per aggiungere o modificare un template.
 * Le modifiche vengono applicate al database solo premendo "Save".
 */
public class TemplateEditorWindow extends Stage {

    private final TemplateDao dao = new TemplateDao();
    private final Template template;
    private final Runnable onSaved;

    private final TextField nome = new TextField();
    private final Label erroreNome = UiUtil.etichettaErrore();
    private final Label erroreTabella = UiUtil.etichettaErrore();
    private final TableView<TemplateItemRow> tabella = new TableView<>();
    private final ObservableList<TemplateItemRow> righe = FXCollections.observableArrayList();
    private final ObservableList<Exercise> eserciziDisponibili = FXCollections.observableArrayList();

    public TemplateEditorWindow(Template template, Runnable onSaved, Window owner) {
        this.template = template;
        this.onSaved = onSaved;

        setTitle(template == null ? "Add template" : "Edit template \"" + template.getName() + "\"");
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        caricaEsercizi();

        BorderPane root = new BorderPane();
        VBox superiore = new VBox(5, new Label("Name*"), nome, erroreNome);
        superiore.setPadding(new Insets(10));
        root.setTop(superiore);
        root.setCenter(creaTabella());
        root.setBottom(creaBarraInferiore());

        Scene scena = new Scene(root, 720, 500);
        setScene(scena);
        setMinWidth(600);
        setMinHeight(420);

        setOnCloseRequest(e -> {
            if (modificheNonSalvate() && !UiUtil.conferma("Sono presenti modifiche non salvate. Annullare e perdere le modifiche?")) {
                e.consume();
            }
        });

        precompila();
    }

    /** Carica gli esercizi disponibili per sceglierli nel template. */
    private void caricaEsercizi() {
        try {
            eserciziDisponibili.setAll(new ExerciseDao().findAll());
        } catch (Exception e) {
            UiUtil.errore("Lettura degli esercizi non riuscita: " + e.getMessage());
        }
    }

    /** Costruisce la tabella con esercizio (selezionabile), set e ripetizioni modificabili. */
    private TableView<TemplateItemRow> creaTabella() {
        TableColumn<TemplateItemRow, Exercise> esercizio = new TableColumn<>("Exercise");
        esercizio.setCellValueFactory(c -> c.getValue().esercizioProperty());
        esercizio.setCellFactory(ComboBoxTableCell.forTableColumn(eserciziDisponibili));
        esercizio.setOnEditCommit(e -> e.getRowValue().esercizioProperty().set(e.getNewValue()));
        esercizio.setPrefWidth(280);

        TableColumn<TemplateItemRow, Integer> sets = new TableColumn<>("Sets");
        sets.setCellValueFactory(c -> c.getValue().setsProperty().asObject());
        sets.setCellFactory(TextFieldTableCell.forTableColumn(converterIntero()));
        sets.setOnEditCommit(e -> {
            if (e.getNewValue() == null) {
                tabella.refresh();
            } else {
                e.getRowValue().setsProperty().set(e.getNewValue());
            }
        });
        sets.setPrefWidth(120);

        TableColumn<TemplateItemRow, Integer> reps = new TableColumn<>("Reps");
        reps.setCellValueFactory(c -> c.getValue().repsProperty().asObject());
        reps.setCellFactory(TextFieldTableCell.forTableColumn(converterIntero()));
        reps.setOnEditCommit(e -> {
            if (e.getNewValue() == null) {
                tabella.refresh();
            } else {
                e.getRowValue().repsProperty().set(e.getNewValue());
            }
        });
        reps.setPrefWidth(120);

        tabella.setEditable(true);
        tabella.getColumns().addAll(esercizio, sets, reps);
        tabella.setItems(righe);
        tabella.setPlaceholder(new Label("Nessun esercizio aggiunto."));
        return tabella;
    }

    /** Converte il testo in numero intero; restituisce null se il testo non e' valido. */
    private StringConverter<Integer> converterIntero() {
        return new StringConverter<>() {
            @Override
            public String toString(Integer valore) {
                return valore == null ? "" : valore.toString();
            }

            @Override
            public Integer fromString(String testo) {
                try {
                    return Integer.parseInt(testo.trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        };
    }

    private VBox creaBarraInferiore() {
        javafx.scene.control.Button aggiungi = UiUtil.bottone("Add Exercise", e -> apriDialogoEsercizio());
        javafx.scene.control.Button rimuovi = UiUtil.bottone("Remove selected", e -> rimuoviSelezionato());
        javafx.scene.control.Button salva = UiUtil.bottone("Save", e -> salva());
        javafx.scene.control.Button annulla = UiUtil.bottone("Cancel", e -> close());
        ToolBar barra = new ToolBar(aggiungi, rimuovi, salva, annulla);
        return new VBox(5, erroreTabella, barra);
    }

    /** Finestra modale per scegliere esercizio, numero di set e ripetizioni. */
    private void apriDialogoEsercizio() {
        Stage dialogo = new Stage();
        dialogo.setTitle("Aggiungi esercizio al template");
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.initOwner(this);

        ComboBox<Exercise> scelta = new ComboBox<>(eserciziDisponibili);
        scelta.setPrefWidth(260);
        Spinner<Integer> set = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        Spinner<Integer> reps = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));

        GridPane griglia = new GridPane();
        griglia.setHgap(10);
        griglia.setVgap(10);
        griglia.setPadding(new Insets(15));
        griglia.addRow(0, new Label("Exercise*"), scelta);
        griglia.addRow(1, new Label("Sets*"), set);
        griglia.addRow(2, new Label("Reps*"), reps);

        javafx.scene.control.Button ok = UiUtil.bottone("OK", e -> {
            Exercise selezionato = scelta.getValue();
            if (selezionato == null) {
                UiUtil.errore("Selezionare un esercizio.");
                return;
            }
            for (TemplateItemRow r : righe) {
                if (r.esercizioProperty().get() != null && r.esercizioProperty().get().getId() == selezionato.getId()) {
                    UiUtil.errore("Questo esercizio e' gia' presente nel template.");
                    return;
                }
            }
            TemplateItemRow riga = new TemplateItemRow();
            riga.esercizioProperty().set(selezionato);
            riga.setsProperty().set(set.getValue());
            riga.repsProperty().set(reps.getValue());
            righe.add(riga);
            dialogo.close();
        });
        javafx.scene.control.Button annulla = UiUtil.bottone("Cancel", e -> dialogo.close());
        HBox bottoni = new HBox(10, ok, annulla);

        VBox root = new VBox(10, griglia, bottoni);
        root.setPadding(new Insets(10));
        dialogo.setScene(new Scene(root, 420, 200));
        dialogo.showAndWait();
    }

    private void rimuoviSelezionato() {
        TemplateItemRow riga = tabella.getSelectionModel().getSelectedItem();
        if (riga == null) {
            UiUtil.errore("Selezionare prima una riga da rimuovere.");
            return;
        }
        righe.remove(riga);
    }

    /** Mostra i dati del template quando si sta modificando. */
    private void precompila() {
        if (template == null) {
            return;
        }
        nome.setText(template.getName());
        for (TemplateItem item : template.getItems()) {
            TemplateItemRow riga = new TemplateItemRow();
            riga.esercizioProperty().set(item.getExercise());
            riga.setsProperty().set(item.getSets());
            riga.repsProperty().set(item.getReps());
            righe.add(riga);
        }
    }

    /** True se i campi sono stati modificati rispetto allo stato iniziale. */
    private boolean modificheNonSalvate() {
        if (!nome.getText().equals(template == null ? "" : template.getName())) {
            return true;
        }
        if (template == null) {
            return !righe.isEmpty();
        }
        return righe.size() != template.getItems().size();
    }

    /** Valida i dati, salva il template e chiude la finestra. */
    private void salva() {
        String nomeTesto = nome.getText() == null ? "" : nome.getText().trim();
        boolean valido = true;
        UiUtil.erroreCampo(erroreNome, null);
        UiUtil.erroreCampo(erroreTabella, null);

        if (nomeTesto.isEmpty()) {
            UiUtil.erroreCampo(erroreNome, "Il nome e' obbligatorio.");
            valido = false;
        }
        if (valido) {
            try {
                int escludiId = template == null ? 0 : template.getId();
                if (dao.esisteNome(nomeTesto, escludiId)) {
                    UiUtil.erroreCampo(erroreNome, "Esiste gia' un template con questo nome.");
                    valido = false;
                }
            } catch (Exception e) {
                UiUtil.errore("Controllo del nome non riuscito: " + e.getMessage());
                return;
            }
        }

        if (righe.isEmpty()) {
            UiUtil.erroreCampo(erroreTabella, "Il template deve contenere almeno un esercizio.");
            valido = false;
        }

        // Controlla che ogni riga abbia set e ripetizioni validi e che gli esercizi siano unici.
        Set<Integer> visti = new HashSet<>();
        for (TemplateItemRow riga : righe) {
            Exercise e = riga.esercizioProperty().get();
            if (e == null) {
                UiUtil.erroreCampo(erroreTabella, "Ogni esercizio deve essere selezionato.");
                valido = false;
                break;
            }
            if (!visti.add(e.getId())) {
                UiUtil.erroreCampo(erroreTabella, "Gli esercizi devono essere unici nel template.");
                valido = false;
                break;
            }
            if (riga.setsProperty().get() < 1 || riga.repsProperty().get() < 1) {
                UiUtil.erroreCampo(erroreTabella, "Ogni esercizio deve avere almeno 1 set e 1 rep.");
                valido = false;
                break;
            }
        }

        if (!valido) {
            return;
        }

        try {
            if (template == null) {
                Template nuovo = new Template(0, nomeTesto);
                inserisciRighe(nuovo);
                dao.insert(nuovo);
            } else {
                template.setName(nomeTesto);
                template.getItems().clear();
                inserisciRighe(template);
                dao.update(template);
            }
            onSaved.run();
            close();
        } catch (Exception e) {
            UiUtil.errore("Salvataggio non riuscito: " + e.getMessage());
        }
    }

    /** Copia le righe della tabella nella lista di items del template, con ordine preservato. */
    private void inserisciRighe(Template t) {
        int posizione = 0;
        for (TemplateItemRow riga : righe) {
            t.getItems().add(new TemplateItem(riga.esercizioProperty().get(), posizione,
                    riga.setsProperty().get(), riga.repsProperty().get()));
            posizione++;
        }
    }
}