package com.athlitrack.ui;

import com.athlitrack.db.TemplateDao;
import com.athlitrack.model.Template;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Finestra dei template: elenco dei nomi.
 * Permette di aggiungere, modificare ed eliminare i template.
 */
public class TemplatesWindow extends Stage {

    private final TemplateDao dao = new TemplateDao();
    private final TableView<Template> tabella = new TableView<>();
    private final ObservableList<Template> dati = FXCollections.observableArrayList();

    public TemplatesWindow() {
        setTitle("AthliTrack - Templates");
        BorderPane root = new BorderPane();
        root.setTop(new Label("Template disponibili"));
        root.setCenter(tabella);
        root.setBottom(creaBarraBottoni());

        TableColumn<Template, String> nome = new TableColumn<>("Name");
        nome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        nome.setPrefWidth(600);
        tabella.getColumns().add(nome);

        tabella.setItems(dati);
        tabella.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tabella.setPlaceholder(new Label("Nessun template presente."));

        Scene scena = new Scene(root, 700, 450);
        setScene(scena);
        setMinWidth(520);
        setMinHeight(360);
        ricarica();
    }

    private ToolBar creaBarraBottoni() {
        javafx.scene.control.Button aggiungi = UiUtil.bottone("Add", e -> apriEditor(null));
        javafx.scene.control.Button modifica = UiUtil.bottone("Edit", e -> apriEditor(selezionato()));
        javafx.scene.control.Button elimina = UiUtil.bottone("Delete", e -> eliminaSelezionato());
        return new ToolBar(aggiungi, modifica, elimina);
    }

    private Template selezionato() {
        return tabella.getSelectionModel().getSelectedItem();
    }

    private void apriEditor(Template template) {
        new TemplateEditorWindow(template, this::ricarica, this).show();
    }

    private void eliminaSelezionato() {
        Template t = selezionato();
        if (t == null) {
            UiUtil.errore("Selezionare prima un template dalla tabella.");
            return;
        }
        if (UiUtil.conferma("Eliminare il template '" + t.getName() + "'?")) {
            try {
                dao.delete(t.getId());
                ricarica();
            } catch (Exception e) {
                UiUtil.errore("Eliminazione non riuscita: " + e.getMessage());
            }
        }
    }

    private void ricarica() {
        try {
            dati.setAll(dao.findAll());
        } catch (Exception e) {
            UiUtil.errore("Lettura dei template non riuscita: " + e.getMessage());
        }
    }
}