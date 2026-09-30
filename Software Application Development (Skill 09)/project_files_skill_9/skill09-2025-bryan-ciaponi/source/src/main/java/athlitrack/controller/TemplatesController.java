package athlitrack.controller;

import athlitrack.App;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.Template;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/** Finestra con tutti i template. */
public class TemplatesController {

    @FXML private TableView<Template> table;

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
                    app.getDb().getTemplates()));
        } catch (DbException e) {
            UiHelpers.showError("Could not load templates:\n" + e.getMessage());
        }
    }

    private Template selected() {
        Template selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiHelpers.showInfo("Please select a template first.");
        }
        return selected;
    }

    @FXML void onAdd() { app.showTemplateEditor(null); }

    @FXML
    void onEdit() {
        Template selected = selected();
        if (selected != null) {
            app.showTemplateEditor(selected.getId());
        }
    }

    @FXML
    void onDelete() {
        Template selected = selected();
        if (selected == null) {
            return;
        }
        if (!UiHelpers.confirmDelete("template '" + selected.getName() + "'")) {
            return;
        }
        try {
            app.getDb().deleteTemplate(selected.getId());
            refresh();
        } catch (DbException e) {
            UiHelpers.showError("Could not delete:\n" + e.getMessage());
        }
    }
}
