package com.athlitrack.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Raccolta di piccoli metodi di utilita' per costruire l'interfaccia.
 * Evita di ripetere il codice di creazione di bottoni e finestre di dialogo.
 */
public final class UiUtil {

    private UiUtil() {
    }

    /** Crea un bottone con testo e azione associata. */
    public static Button bottone(String testo, EventHandler<ActionEvent> azione) {
        Button b = new Button(testo);
        b.setOnAction(azione);
        return b;
    }

    /** Mostra un messaggio di errore. */
    public static void errore(String messaggio) {
        Alert avviso = new Alert(Alert.AlertType.ERROR, messaggio);
        avviso.setHeaderText("Errore");
        avviso.showAndWait();
    }

    /** Mostra un messaggio informativo. */
    public static void informazione(String messaggio) {
        Alert avviso = new Alert(Alert.AlertType.INFORMATION, messaggio);
        avviso.setHeaderText("Informazione");
        avviso.showAndWait();
    }

    /**
     * Chiede conferma all'utente (finestra "sei sicuro?").
     * Restituisce true se l'utente preme Conferma.
     */
    public static boolean conferma(String messaggio) {
        Alert avviso = new Alert(Alert.AlertType.CONFIRMATION, messaggio, ButtonType.OK, ButtonType.CANCEL);
        avviso.setHeaderText("Conferma");
        return avviso.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /** Imposta il messaggio di errore di un'etichetta sotto un campo. */
    public static void erroreCampo(Label etichetta, String messaggio) {
        if (messaggio == null) {
            etichetta.setText("");
            etichetta.setVisible(false);
            etichetta.setManaged(false);
        } else {
            etichetta.setText(messaggio);
            etichetta.setVisible(true);
            etichetta.setManaged(true);
        }
    }

    /** Crea un'etichetta rossa nascosta per mostrare gli errori dei form. */
    public static Label etichettaErrore() {
        Label l = new Label();
        l.setStyle("-fx-text-fill: red;");
        l.setWrapText(true);
        l.setVisible(false);
        l.setManaged(false);
        return l;
    }

    /** Etichetta con testo in stile titolo. */
    public static Label titolo(String testo) {
        Label l = new Label(testo);
        l.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        return l;
    }

    /** Una coppia "testo: valore" mostrata come etichetta. */
    public static Label coppia(String testo, String valore) {
        return new Label(testo + ": " + valore);
    }

    /** Configura la dimensione minima di una finestra. */
    public static void dimensioneMinima(Region root, double larghezza, double altezza) {
        root.setMinWidth(larghezza);
        root.setMinHeight(altezza);
    }
}