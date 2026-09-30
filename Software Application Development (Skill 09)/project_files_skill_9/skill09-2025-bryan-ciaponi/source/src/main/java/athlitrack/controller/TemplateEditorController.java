package athlitrack.controller;

import athlitrack.App;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.Exercise;
import athlitrack.model.Template;
import athlitrack.model.TemplateItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "Aggiungi template" / "Modifica template ...": nome più righe esercizio ordinate
 * (esercizio + serie + ripetizioni, tutto obbligatorio, esercizi unici).
 */
public class TemplateEditorController {

    @FXML private Label title;
    @FXML private TextField nameField;
    @FXML private TableView<TemplateItem> table;
    @FXML private ComboBox<String> exerciseBox;
    @FXML private Spinner<Integer> setsSpinner;
    @FXML private Spinner<Integer> repsSpinner;
    @FXML private Label error;

    /** Righe in memoria (ordine preservato); salvate solo col pulsante Salva. */
    private final ObservableList<TemplateItem> rows = FXCollections.observableArrayList();
    private final Map<String, Integer> exerciseIds = new HashMap<>();
    private App app;
    private Integer templateId;
    private String snapshot;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        repsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 10));
        setsSpinner.setEditable(true);
        repsSpinner.setEditable(true);
        table.setItems(rows);
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        exerciseBox.setValue(selected.getExerciseName());
                        setsSpinner.getValueFactory().setValue(selected.getSets());
                        repsSpinner.getValueFactory().setValue(selected.getReps());
                    }
                });
    }

    /** templateId null = modalità aggiunta; altrimenti modifica. */
    public void refresh(Integer templateId) {
        this.templateId = templateId;
        error.setText("");
        // Scelte esercizio dal database.
        exerciseIds.clear();
        List<String> names = new ArrayList<>();
        for (Exercise e : app.getDb().getExercises()) {
            exerciseIds.put(e.getName(), e.getId());
            names.add(e.getName());
        }
        exerciseBox.setItems(FXCollections.observableArrayList(names));
        rows.clear();
        if (templateId == null) {
            title.setText("Add template");
            nameField.clear();
        } else {
            Template tpl = app.getDb().getTemplate(templateId);
            title.setText("Edit template \"" + tpl.getName() + "\"");
            nameField.setText(tpl.getName());
            rows.addAll(tpl.getItems());
        }
        if (!names.isEmpty()) {
            exerciseBox.setValue(names.get(0));
        }
        snapshot = state();
    }

    private String state() {
        StringBuilder sb = new StringBuilder(nameField.getText());
        for (TemplateItem r : rows) {
            sb.append('|').append(r.getExerciseId())
              .append(':').append(r.getSets()).append('x').append(r.getReps());
        }
        return sb.toString();
    }

    private TemplateItem readRowForm() {
        String name = exerciseBox.getValue();
        if (name == null || name.trim().isEmpty()) {
            error.setText("Please select an exercise.");
            return null;
        }
        if (!exerciseIds.containsKey(name)) {
            error.setText("Unknown exercise selected.");
            return null;
        }
        int sets, reps;
        try {
            sets = setsSpinner.getValue();
            reps = repsSpinner.getValue();
        } catch (Exception e) {
            error.setText("Sets and reps must be whole numbers.");
            return null;
        }
        if (sets < 1 || reps < 1) {
            error.setText("Sets and reps must be at least 1.");
            return null;
        }
        error.setText("");
        return new TemplateItem(exerciseIds.get(name), name, sets, reps);
    }

    @FXML
    void onAddRow() {
        TemplateItem row = readRowForm();
        if (row == null) {
            return;
        }
        // Gli esercizi devono essere unici dentro un template (spec).
        for (TemplateItem r : rows) {
            if (r.getExerciseId() == row.getExerciseId()) {
                error.setText("'" + row.getExerciseName() + "' is already in this template.");
                return;
            }
        }
        rows.add(row);
    }

    @FXML
    void onUpdateRow() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            error.setText("Select a row to update first.");
            return;
        }
        TemplateItem row = readRowForm();
        if (row == null) {
            return;
        }
        for (int i = 0; i < rows.size(); i++) {
            if (i != index && rows.get(i).getExerciseId() == row.getExerciseId()) {
                error.setText("'" + row.getExerciseName() + "' is already in this template.");
                return;
            }
        }
        row.setReps(row.getReps() + 10);
        rows.set(index, row);
    }

    @FXML
    void onRemoveRow() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            error.setText("Select a row to remove first.");
            return;
        }
        rows.remove(index);
        error.setText("");
    }

    /** Tiene modificabile l'ordine degli esercizi (spec: l'ordine va preservato). */
    private void move(int direction) {
        int index = table.getSelectionModel().getSelectedIndex();
        int other = index + direction;
        if (index < 0 || other < 0 || other >= rows.size()) {
            return;
        }
        TemplateItem tmp = rows.get(index);
        rows.set(index, rows.get(other));
        rows.set(other, tmp);
        table.getSelectionModel().select(other);
    }

    @FXML void onMoveUp() { move(-1); }

    @FXML void onMoveDown() { move(1); }

    @FXML
    void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            error.setText("Name is mandatory.");
            return;
        }
        if (rows.isEmpty()) {
            error.setText("A template needs at least one exercise "
                    + "with at least one set and reps per set.");
            return;
        }
        try {
            app.getDb().saveTemplate(templateId, name, new ArrayList<>(rows));
        } catch (DbException e) {
            error.setText(e.getMessage());
            return;
        }
        app.showTemplates();
    }

    @FXML
    void onCancel() {
        if (!state().equals(snapshot) && !UiHelpers.confirmDiscard()) {
            return;
        }
        app.showTemplates();
    }
}
