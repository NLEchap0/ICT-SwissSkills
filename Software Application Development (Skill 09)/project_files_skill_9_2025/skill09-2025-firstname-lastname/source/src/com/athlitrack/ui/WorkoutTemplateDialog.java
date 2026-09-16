package com.athlitrack.ui;

import com.athlitrack.model.Template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Finestra modale mostrata all'apertura della finestra "Add Workout".
 * Permette di scegliere un template per pre-compilare gli esercizi,
 * oppure di iniziare senza template.
 *
 * Restituisce: null se l'utente annulla, -1 se continua senza template,
 * altrimenti l'id del template scelto.
 */
public class WorkoutTemplateDialog {

    /** Esito della scelta. null = annullato, -1 = senza template, altrimenti id template. */
    private Integer scelta;

    public static Integer scegliTemplate(Window owner, java.util.List<Template> templates) {
        WorkoutTemplateDialog dialogo = new WorkoutTemplateDialog();
        return dialogo.mostra(owner, templates);
    }

    private Integer mostra(Window owner, java.util.List<Template> templates) {
        Stage palco = new Stage();
        palco.setTitle("Scegli un template");
        palco.initModality(Modality.APPLICATION_MODAL);
        palco.initOwner(owner);

        ObservableList<Template> dati = FXCollections.observableArrayList(templates);
        ListView<Template> lista = new ListView<>(dati);
        lista.setPrefHeight(200);
        lista.setPlaceholder(new Label("Nessun template disponibile."));

        Label messaggio = new Label();
        if (templates.isEmpty()) {
            messaggio.setText("Nessun template disponibile. Il workout partira' senza esercizi precompilati.");
        } else {
            messaggio.setText("Seleziona un template per precompilare gli esercizi, oppure continua senza template.");
        }
        messaggio.setWrapText(true);

        Button usa = new Button("Usa template");
        usa.setOnAction(e -> {
            Template t = lista.getSelectionModel().getSelectedItem();
            if (t == null) {
                UiUtil.errore("Selezionare prima un template dalla lista.");
                return;
            }
            scelta = t.getId();
            palco.close();
        });

        Button senza = new Button("Continua senza template");
        senza.setOnAction(e -> {
            scelta = -1;
            palco.close();
        });

        Button annulla = new Button("Cancel");
        annulla.setOnAction(e -> {
            scelta = null;
            palco.close();
        });

        HBox bottoni = new HBox(10, usa, senza, annulla);
        VBox root = new VBox(10, messaggio, lista, bottoni);
        root.setPadding(new Insets(10));
        palco.setScene(new Scene(root, 480, 320));
        palco.showAndWait();
        return scelta;
    }
}