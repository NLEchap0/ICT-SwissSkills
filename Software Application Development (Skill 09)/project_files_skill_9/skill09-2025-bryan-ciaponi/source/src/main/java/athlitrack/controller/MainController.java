package athlitrack.controller;

import athlitrack.App;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.WorkoutSummary;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** Prima finestra: lista dei workout creati. */
public class MainController {

    @FXML private TableView<WorkoutSummary> table;
    @FXML private TableColumn<WorkoutSummary, String> startColumn;
    @FXML private TableColumn<WorkoutSummary, String> endColumn;
    @FXML private TableColumn<WorkoutSummary, String> durationColumn;

    private App app;

    public void setApp(App app) {
        this.app = app;
        refresh();
    }

    @FXML
    void initialize() {
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // Inizio/fine/durata sono calcolati dalle date (si impostano qui nel codice).
        startColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                UiHelpers.formatDateTime(cell.getValue().getStart())));
        endColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                UiHelpers.formatDateTime(cell.getValue().getEnd())));
        durationColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                UiHelpers.formatDuration(cell.getValue().getStart(),
                        cell.getValue().getEnd())));
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(
                    app.getDb().getWorkouts()));
        } catch (DbException e) {
            UiHelpers.showError("Could not load workouts:\n" + e.getMessage());
        }
    }

    private WorkoutSummary selected() {
        WorkoutSummary selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelpers.showInfo("Please select a workout first.");
        }
        return selected;
    }

    @FXML void onAdd() { app.showWorkoutEditor(null); }

    @FXML
    void onEdit() {
        WorkoutSummary selected = selected();
        if (selected != null) {
            app.showWorkoutEditor(selected.getId());
        }
    }

    @FXML
    void onView() {
        WorkoutSummary selected = selected();
        if (selected != null) {
            app.showWorkoutDetail(selected.getId());
        }
    }

    @FXML
    void onDelete() {
        WorkoutSummary selected = selected();
        if (selected == null) {
            return;
        }
        if (!UiHelpers.confirmDelete("workout '" + selected.getName() + "'")) {
            return;
        }
        try {
            app.getDb().deleteWorkout(selected.getId());
            refresh();
        } catch (DbException e) {
            UiHelpers.showError("Could not delete:\n" + e.getMessage());
        }
    }
}
