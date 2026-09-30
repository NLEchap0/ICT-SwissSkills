package athlitrack.controller;

import athlitrack.App;
import athlitrack.Database;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.Exercise;
import athlitrack.model.SetEntry;
import athlitrack.model.TemplateItem;
import athlitrack.model.Workout;
import athlitrack.model.WorkoutExercise;
import athlitrack.model.WorkoutSummary;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "Aggiungi workout" / "Modifica workout ...": nome, date/ore inizio-fine,
 * esercizi ordinati ognuno con le sue serie (peso + ripetizioni).
 */
public class WorkoutEditorController {

    @FXML private Label title;
    @FXML private TextField nameField;
    @FXML private DatePicker startDate;
    @FXML private TextField startTime;
    @FXML private DatePicker endDate;
    @FXML private TextField endTime;
    @FXML private Label error;
    @FXML private ListView<String> exerciseList;
    @FXML private ComboBox<String> exerciseBox;
    @FXML private TableView<SetEntry> setsTable;
    @FXML private TableColumn<SetEntry, Integer> setColumn;
    @FXML private TableColumn<SetEntry, Double> weightColumn;
    @FXML private TableColumn<SetEntry, Integer> repsColumn;
    @FXML private TextField weightField;
    @FXML private TextField repsField;

    /** Modello in memoria (ordine preservato); salvato solo col pulsante Salva. */
    private final List<WorkoutExercise> items = new ArrayList<>();
    private final Map<String, Integer> exerciseIds = new HashMap<>();
    private App app;
    private Integer workoutId;
    private String snapshot;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        setsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // Numeri di serie automatici 1..n, non modificabili (spec).
        setColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
                setsTable.getItems().indexOf(cell.getValue()) + 1));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        repsColumn.setCellValueFactory(new PropertyValueFactory<>("reps"));
        exerciseList.getSelectionModel().selectedIndexProperty().addListener(
                (obs, old, val) -> redrawSets());
        // Click su una riga la carica nei campi peso/ripetizioni (modifica inline).
        setsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        weightField.setText(String.valueOf(selected.getWeight()));
                        repsField.setText(String.valueOf(selected.getReps()));
                    }
                });
    }

    /** workoutId null = modalità aggiunta (apre la scelta template); altrimenti modifica. */
    public void refresh(Integer workoutId) {
        this.workoutId = workoutId;
        error.setText("");
        exerciseIds.clear();
        List<String> names = new ArrayList<>();
        for (Exercise e : app.getDb().getExercises()) {
            exerciseIds.put(e.getName(), e.getId());
            names.add(e.getName());
        }
        exerciseBox.setItems(FXCollections.observableArrayList(names));
        if (!names.isEmpty()) {
            exerciseBox.setValue(names.get(0));
        }
        items.clear();
        weightField.clear();
        repsField.clear();

        if (workoutId == null) {
            title.setText("Add workout");
            nameField.clear();
            fillNow();
            redrawExercises(-1);
            snapshot = state();
            // Modale scelta template all'avvio (spec); pre-compila dopo la scelta.
            Platform.runLater(this::openTemplatePicker);
        } else {
            Workout w = app.getDb().getWorkout(workoutId);
            title.setText("Edit workout \"" + w.getName() + "\"");
            nameField.setText(w.getName());
            setDateTime(w.getStart(), startDate, startTime);
            setDateTime(w.getEnd(), endDate, endTime);
            for (WorkoutExercise we : w.getExercises()) {
                WorkoutExercise copy = new WorkoutExercise(
                        we.getExerciseId(), we.getExerciseName());
                for (SetEntry s : we.getSets()) {
                    copy.getSets().add(new SetEntry(s.getWeight(), s.getReps()));
                }
                items.add(copy);
            }
            redrawExercises(items.isEmpty() ? -1 : 0);
            snapshot = state();
        }
    }

    private void openTemplatePicker() {
        App.Loaded<TemplatePickerController> dialog =
                App.loadView("TemplatePickerDialog.fxml");
        TemplatePickerController controller = dialog.controller();
        controller.setApp(app);
        controller.setOnPick(this::applyTemplateChoice);

        Stage stage = new Stage();
        stage.setTitle("Start workout from template?");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.initOwner(app.getStage());
        stage.setScene(new Scene(dialog.root()));
        stage.setOnCloseRequest(e -> controller.pickOnClose());
        stage.showAndWait();
    }

    private void setDateTime(LocalDateTime dt, DatePicker date, TextField time) {
        date.setValue(dt.toLocalDate());
        time.setText(String.format("%02d:%02d", dt.getHour(), dt.getMinute()));
    }

    @FXML
    void onFillNow() {
        fillNow();
    }

    private void fillNow() {
        LocalDateTime start = LocalDateTime.now().withSecond(0).withNano(0);
        setDateTime(start, startDate, startTime);
        setDateTime(start.plusHours(1), endDate, endTime);
    }

    /**
     * Pre-compila gli esercizi dal template scelto (spec): serie/ripetizioni
     * vengono dal template, il peso va inserito a mano (parte da 0).
     */
    private void applyTemplateChoice(Integer templateId) {
        if (templateId == null) {
            snapshot = state();
            return;
        }
        athlitrack.model.Template tpl = app.getDb().getTemplate(templateId);
        items.clear();
        for (TemplateItem te : tpl.getItems()) {
            WorkoutExercise we = new WorkoutExercise(
                    te.getExerciseId(), te.getExerciseName());
            for (int i = 0; i < te.getSets(); i++) {
                we.getSets().add(new SetEntry(0, te.getReps()));
            }
            items.add(we);
        }
        redrawExercises(items.isEmpty() ? -1 : 0);
        snapshot = state(); // il pre-compilato è il nuovo stato "pulito"
    }

    private String state() {
        StringBuilder sb = new StringBuilder(nameField.getText())
                .append('|').append(startDate.getValue())
                .append('|').append(startTime.getText())
                .append('|').append(endDate.getValue())
                .append('|').append(endTime.getText());
        for (WorkoutExercise we : items) {
            sb.append('|').append(we.getExerciseId()).append(':');
            for (SetEntry s : we.getSets()) {
                sb.append(s.getWeight()).append('x').append(s.getReps()).append(',');
            }
        }
        return sb.toString();
    }

    private int selectedExercise() {
        return exerciseList.getSelectionModel().getSelectedIndex();
    }

    // ---------------------------------------------------------- exercises --
    private void redrawExercises(int select) {
        ObservableList<String> labels = FXCollections.observableArrayList();
        for (WorkoutExercise we : items) {
            labels.add(we.getExerciseName() + " (" + we.getSets().size() + " sets)");
        }
        exerciseList.setItems(labels);
        if (select >= 0 && select < labels.size()) {
            exerciseList.getSelectionModel().select(select);
        }
        redrawSets();
    }

    @FXML
    void onAddExercise() {
        String name = exerciseBox.getValue();
        if (name == null || name.trim().isEmpty()) {
            error.setText("Please select an exercise to add.");
            return;
        }
        if (!exerciseIds.containsKey(name)) {
            error.setText("Unknown exercise selected.");
            return;
        }
        int id = exerciseIds.get(name);
        for (WorkoutExercise we : items) {
            if (we.getExerciseId() == id) {
                error.setText("'" + name + "' is already in this workout "
                        + "(exercises must be unique).");
                return;
            }
        }
        items.add(new WorkoutExercise(id, name));
        error.setText("");
        redrawExercises(items.size() - 1);
    }

    @FXML
    void onRemoveExercise() {
        int index = selectedExercise();
        if (index < 0) {
            error.setText("Select an exercise to remove first.");
            return;
        }
        items.remove(index);
        error.setText("");
        redrawExercises(items.isEmpty() ? -1 : Math.min(index, items.size() - 1));
    }

    private void moveExercise(int direction) {
        int index = selectedExercise();
        int other = index + direction;
        if (index < 0 || other < 0 || other >= items.size()) {
            return;
        }
        WorkoutExercise tmp = items.get(index);
        items.set(index, items.get(other));
        items.set(other, tmp);
        redrawExercises(other);
    }

    @FXML void onMoveUp() { moveExercise(-1); }

    @FXML void onMoveDown() { moveExercise(1); }

    // --------------------------------------------------------------- sets --
    private void redrawSets() {
        int index = selectedExercise();
        if (index < 0) {
            setsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        setsTable.setItems(FXCollections.observableArrayList(items.get(index).getSets()));
    }

    /** Rinfresca le etichette "N serie" dopo le modifiche alle serie. */
    private void refreshExerciseLabels() {
        redrawExercises(selectedExercise());
    }

    private SetEntry readSetForm() {
        double weight;
        int reps;
        try {
            weight = Double.parseDouble(weightField.getText().trim().replace(',', '.'));
            reps = (int) Double.parseDouble(repsField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException e) {
            error.setText("Weight and reps must be numbers.");
            return null;
        }
        if (weight < 0) {
            error.setText("Weight cannot be negative.");
            return null;
        }
        if (reps < 1) {
            error.setText("Reps must be at least 1.");
            return null;
        }
        error.setText("");
        return new SetEntry(weight, reps);
    }

    @FXML
    void onAddSet() {
        int index = selectedExercise();
        if (index < 0) {
            error.setText("Add an exercise first, then its sets.");
            return;
        }
        SetEntry set = readSetForm();
        if (set == null) {
            return;
        }
        items.get(index).getSets().add(set); // numerate in automatico (spec)
        weightField.clear();
        repsField.clear();
        refreshExerciseLabels();
    }

    @FXML
    void onUpdateSet() {
        int index = selectedExercise();
        int row = setsTable.getSelectionModel().getSelectedIndex();
        if (index < 0 || row < 0) {
            error.setText("Select a set row to update first.");
            return;
        }
        SetEntry set = readSetForm();
        if (set == null) {
            return;
        }
        items.get(index).getSets().set(row, set);
        redrawSets();
    }

    @FXML
    void onDeleteSet() {
        int index = selectedExercise();
        int row = setsTable.getSelectionModel().getSelectedIndex();
        if (index < 0 || row < 0) {
            error.setText("Select a set row to delete first.");
            return;
        }
        items.get(index).getSets().remove(row);
        refreshExerciseLabels();
    }

    // --------------------------------------------------------------- save --
    private LocalDateTime readDateTime(DatePicker date, TextField time, String field) {
        LocalDate day = date.getValue();
        LocalTime hour = UiHelpers.parseTime(time.getText());
        if (day == null || hour == null) {
            error.setText(field + " must be a valid date and time (HH:mm).");
            return null;
        }
        return LocalDateTime.of(day, hour);
    }

    @FXML
    void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            error.setText("Name is mandatory.");
            return;
        }
        LocalDateTime start = readDateTime(startDate, startTime, "Start");
        LocalDateTime end = readDateTime(endDate, endTime, "End");
        if (start == null || end == null) {
            return;
        }
        if (!end.isAfter(start)) {
            error.setText("End must be after start.");
            return;
        }
        // I workout non devono sovrapporsi (spec).
        WorkoutSummary clash = app.getDb().findOverlap(start, end, workoutId);
        if (clash != null) {
            error.setText(UiHelpers.overlapText(clash));
            return;
        }
        if (app.getDb().workoutNameTaken(name, workoutId)) {
            error.setText("A workout named '" + name + "' already exists.");
            return;
        }
        if (items.isEmpty()) {
            error.setText("A workout needs at least one exercise.");
            return;
        }
        for (WorkoutExercise we : items) {
            if (we.getSets().isEmpty()) {
                error.setText("Exercise '" + we.getExerciseName()
                        + "' needs at least one set "
                        + "(fill weight/reps and press 'Add set').");
                return;
            }
        }
        try {
            app.getDb().saveWorkout(workoutId, name, start, end, items);
        } catch (DbException e) {
            error.setText(e.getMessage());
            return;
        }
        app.showMain();
    }

    @FXML
    void onCancel() {
        if (!state().equals(snapshot) && !UiHelpers.confirmDiscard()) {
            return;
        }
        app.showMain();
    }
}
