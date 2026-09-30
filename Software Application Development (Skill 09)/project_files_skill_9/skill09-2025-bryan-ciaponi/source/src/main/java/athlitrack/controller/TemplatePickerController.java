package athlitrack.controller;

import athlitrack.App;
import athlitrack.model.Template;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

/**
 * Modale mostrata creando un workout: scegli un template per pre-compilare
 * gli esercizi, oppure parti vuoto. Mostra un messaggio se non ci sono template.
 */
public class TemplatePickerController {

    @FXML private Label message;
    @FXML private ListView<String> list;
    @FXML private Button useButton;

    private List<Template> templates;
    private Consumer<Integer> onPick;
    private boolean resolved; // evita di chiamare onPick due volte (pulsante + chiusura)

    public void setApp(App app) {
        templates = app.getDb().getTemplates();
        if (templates.isEmpty()) {
            message.setText("No templates available.\n"
                    + "Start with an empty workout and add exercises manually.");
            list.setVisible(false);
            useButton.setVisible(false);
        } else {
            for (Template t : templates) {
                list.getItems().add(t.getName());
            }
            list.getSelectionModel().selectFirst();
        }
    }

    public void setOnPick(Consumer<Integer> onPick) {
        this.onPick = onPick;
    }

    /** Chiudere con la X vale come "parti vuoto" (default sensato). */
    public boolean pickOnClose() {
        if (!resolved) {
            resolved = true;
            onPick.accept(null);
        }
        return resolved;
    }

    private void close() {
        ((Stage) message.getScene().getWindow()).close();
    }

    private void pick(Integer templateId) {
        if (resolved) {
            return;
        }
        resolved = true;
        onPick.accept(templateId);
        close();
    }

    @FXML
    void onUseTemplate() {
        int index = list.getSelectionModel().getSelectedIndex();
        pick(templates.get(Math.max(index, 0)).getId());
    }

    @FXML
    void onStartEmpty() {
        pick(null);
    }
}
