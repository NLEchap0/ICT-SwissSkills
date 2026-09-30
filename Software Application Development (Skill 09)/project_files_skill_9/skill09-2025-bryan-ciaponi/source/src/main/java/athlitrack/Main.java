package athlitrack;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Punto di ingresso. Tenuto separato da App di proposito: quando le classi
 * JavaFX sono dentro un fat jar, il Main-Class NON deve estendere Application.
 */
public class Main {

    public static void main(String[] args) {
        // Gestione globale degli errori: mostra un dialogo invece di chiudere il programma (spec).
        Thread.setDefaultUncaughtExceptionHandler((thread, error) ->
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            errorToMessage(error), ButtonType.OK);
                    alert.setTitle("Unexpected error");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }));
        Application.launch(App.class, args);
    }

    private static String errorToMessage(Throwable error) {
        // Messaggio semplice per l'utente: tipo di errore + dettaglio + rassicurazione.
        String detail = error.getMessage() == null ? "" : ": " + error.getMessage();
        return error.getClass().getSimpleName() + detail
                + "\n\nThe application will keep running.";
    }
}
