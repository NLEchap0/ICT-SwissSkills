package athlitrack.controller;

import athlitrack.App;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.Exercise;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** Finestra con tutti gli esercizi e azioni aggiungi/modifica/elimina/progressi. */
public class ExercisesController {

    @FXML private TableView<Exercise> table;

    private App app;

    public void setApp(App app) {
        this.app = app;
        refresh();
    }

    @FXML
    void initialize() {
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(
                    app.getDb().getExercises()));
        } catch (DbException e) {
            UiHelpers.showError("Could not load exercises:\n" + e.getMessage());
        }
    }

    private Exercise selected() {
        Exercise selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelpers.showInfo("Please select an exercise first.");
        }
        return selected;
    }

    /** Dialogo modale aggiungi/modifica; la tabella si aggiorna alla chiusura. */
    private void openDialog(Integer exerciseId) {
        App.Loaded<ExerciseDialogController> dialog =
                App.loadView("ExerciseDialog.fxml");
        dialog.controller().setApp(app);
        dialog.controller().setExerciseId(exerciseId);

        Stage stage = new Stage();
        stage.setTitle(exerciseId == null ? "Add exercise" : "Edit exercise");
        stage.initModality(Modality.APPLICATION_MODAL); // modale a dimensione fissa (spec)
        stage.setResizable(false);
        stage.initOwner(app.getStage());
        stage.setScene(new Scene(dialog.root()));
        stage.setOnCloseRequest(e -> {
            if (dialog.controller().isDirty() && !UiHelpers.confirmDiscard()) {
                e.consume(); // tiene aperto il dialogo
            }
        });
        stage.showAndWait();
        refresh();
    }

    @FXML void onAdd() { openDialog(null); }

    @FXML
    void onEdit() {
        Exercise selected = selected();
        if (selected != null) {
            openDialog(selected.getId());
        }
    }

    @FXML
    void onDelete() {
        Exercise selected = selected();
        if (selected == null) {
            return;
        }
        try {
            if (app.getDb().exerciseInUse(selected.getId())) {
                UiHelpers.showWarning("Exercise '" + selected.getName()
                        + "' is used in a template or workout\nand cannot be deleted.");
                return;
            }
        } catch (DbException e) {
            UiHelpers.showError(e.getMessage());
            return;
        }
        if (!UiHelpers.confirmDelete("exercise '" + selected.getName() + "'")) {
            return;
        }
        try {
            app.getDb().deleteExercise(selected.getId());
            refresh();
        } catch (DbException e) {
            UiHelpers.showError("Could not delete:\n" + e.getMessage());
        }
    }

    @FXML
    void onProgress() {
        Exercise selected = selected();
        if (selected != null) {
            app.showProgress(selected.getId(), "Exercises");
        }
    }
}
