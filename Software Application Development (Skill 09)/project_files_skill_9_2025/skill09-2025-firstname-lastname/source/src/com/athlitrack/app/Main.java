package com.athlitrack.app;

import com.athlitrack.db.Database;
import com.athlitrack.ui.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione AthliTrack.
 * Inizializza il database e apre la finestra principale.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Gestore globale: evita che un errore imprevisto faccia chiudere il programma.
        Thread.setDefaultUncaughtExceptionHandler((thread, errore) -> {
            errore.printStackTrace();
            Platform.runLater(() -> {
                Alert avviso = new Alert(Alert.AlertType.ERROR,
                        "Si e' verificato un errore imprevisto:\n" + errore.getMessage());
                avviso.setHeaderText("Errore");
                avviso.showAndWait();
            });
        });

        try {
            Database.inizializza();
            new MainWindow().show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert avviso = new Alert(Alert.AlertType.ERROR,
                    "Impossibile avviare l'applicazione:\n" + e.getMessage());
            avviso.setHeaderText("Errore di avvio");
            avviso.showAndWait();
        }
    }
}