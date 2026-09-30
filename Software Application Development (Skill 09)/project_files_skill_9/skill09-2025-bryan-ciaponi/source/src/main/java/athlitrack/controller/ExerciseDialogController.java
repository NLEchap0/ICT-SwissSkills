package athlitrack.controller;

import athlitrack.App;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.Exercise;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

/**
 * Dialogo modale aggiungi/modifica esercizio.
 * Non salva niente finché non si preme Salva; Annulla ripristina (dopo conferma).
 */
public class ExerciseDialogController {

    private static final List<String> TYPES =
            Arrays.asList("barbell", "dumbbell", "cable", "machine");

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> typeBox;
    @FXML private Label error;

    private App app;
    private Integer exerciseId;
    private String snapshot;

    public void setApp(App app) {
        this.app = app;
    }

    public void setExerciseId(Integer exerciseId) {
        this.exerciseId = exerciseId;
        typeBox.setItems(FXCollections.observableArrayList(TYPES));
        if (exerciseId != null) {
            Exercise existing = app.getDb().getExercise(exerciseId);
            nameField.setText(existing.getName());
            descriptionField.setText(existing.getDescription());
            typeBox.setValue(existing.getType());
        }
        snapshot = state();
    }

    private String state() {
        String value = typeBox.getValue() == null ? "" : typeBox.getValue();
        String typed = typeBox.getEditor().getText();
        return nameField.getText() + "|" + descriptionField.getText()
                + "|" + (typed == null || typed.isEmpty() ? value : typed);
    }

    public boolean isDirty() {
        return snapshot != null && !state().equals(snapshot);
    }

    private void close() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    void onSave() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = typeBox.getEditor().getText().trim();
        if (type.isEmpty() && typeBox.getValue() != null) {
            type = typeBox.getValue().trim();
        }
        // Tutti i campi sono obbligatori (spec).
        if (name.isEmpty() || description.isEmpty() || type.isEmpty()) {
            error.setText("All fields are mandatory.");
            return;
        }
        try {
            if (exerciseId == null) {
                app.getDb().addExercise(name, description, type);
            } else {
                app.getDb().updateExercise(exerciseId, name, description, type);
            }
        } catch (DbException e) {
            error.setText(e.getMessage());
            return;
        }
        snapshot = state(); // pulito: la chiusura non chiede più conferma
        close();
    }

    @FXML
    void onCancel() {
        // Annullare ripristina le modifiche dopo conferma (spec).
        if (isDirty() && !UiHelpers.confirmDiscard()) {
            return;
        }
        close();
    }
}
