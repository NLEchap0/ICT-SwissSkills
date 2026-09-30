package athlitrack.controller;

import athlitrack.App;
import athlitrack.Database;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.SetEntry;
import athlitrack.model.Workout;
import athlitrack.model.WorkoutExercise;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

/** Pagina di sola lettura "Workout ..." con statistiche e scorciatoia ai progressi. */
public class WorkoutDetailController {

    /** Una riga piatta della tabella (esercizio / serie / peso / ripetizioni). */
    public static class DetailRow {
        private final String exercise;
        private final String set;
        private final String weight;
        private final String reps;

        DetailRow(String exercise, String set, String weight, String reps) {
            this.exercise = exercise;
            this.set = set;
            this.weight = weight;
            this.reps = reps;
        }

        public String getExercise() { return exercise; }
        public String getSet() { return set; }
        public String getWeight() { return weight; }
        public String getReps() { return reps; }
    }

    @FXML private Label title;
    @FXML private TableView<DetailRow> table;
    @FXML private Label stats;
    @FXML private ComboBox<String> exerciseBox;

    private App app;
    private final List<Integer> exerciseIds = new ArrayList<>();
    private int workoutId;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    public void refresh(int workoutId) {
        this.workoutId = workoutId;
        Workout w;
        try {
            w = app.getDb().getWorkout(workoutId);
        } catch (DbException e) {
            UiHelpers.showError("Could not load workout:\n" + e.getMessage());
            app.showMain();
            return;
        }
        title.setText("Workout \"" + w.getName() + "\"");

        List<DetailRow> rows = new ArrayList<>();
        exerciseIds.clear();
        List<String> names = new ArrayList<>();
        for (WorkoutExercise we : w.getExercises()) {
            exerciseIds.add(we.getExerciseId());
            names.add(we.getExerciseName());
            if (we.getSets().isEmpty()) {
                rows.add(new DetailRow(we.getExerciseName(), "-", "-", "-"));
            }
            int number = 1;
            for (SetEntry s : we.getSets()) {
                rows.add(new DetailRow(we.getExerciseName(), String.valueOf(number),
                        UiHelpers.formatVolume(s.getWeight()),
                        String.valueOf(s.getReps())));
                number++;
            }
        }
        table.setItems(FXCollections.observableArrayList(rows));

        double[] totals = Database.workoutStats(w);
        stats.setText("Total volume: " + UiHelpers.formatVolume(totals[0]) + " kg"
                + "   |   Total sets: " + (int) totals[1]
                + "   |   Total reps: " + (int) totals[2]);

        exerciseBox.setItems(FXCollections.observableArrayList(names));
        if (!names.isEmpty()) {
            exerciseBox.setValue(names.get(0));
        }
    }

    @FXML
    void onProgress() {
        int index = exerciseBox.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            UiHelpers.showInfo("Please select an exercise first.");
            return;
        }
        // Ricorda dove tornare: la pagina dettaglio di questo workout.
        app.showProgress(exerciseIds.get(index), "Workout:" + workoutId);
    }

    @FXML
    void onBack() {
        app.showMain();
    }
}
