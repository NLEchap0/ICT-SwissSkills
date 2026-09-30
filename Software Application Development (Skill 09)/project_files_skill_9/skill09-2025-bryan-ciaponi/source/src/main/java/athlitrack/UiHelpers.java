package athlitrack;

import athlitrack.model.WorkoutSummary;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** Piccoli aiuti condivisi per la UI: dialoghi, etichette, formattazioni. */
public final class UiHelpers {

    private UiHelpers() { } // classe solo statica, non si istanzia

    /** Durata come "2h 30m" tra due date/ore. */
    public static String formatDuration(LocalDateTime start, LocalDateTime end) {
        long mins = Duration.between(start, end).toMinutes();
        return (mins / 60) + "h " + String.format("%02d", mins % 60) + "m";
    }

    /** Colonne inizio/fine della tabella workout ("2025-03-10 18:30"). */
    public static String formatDateTime(LocalDateTime dt) {
        return dt.format(Database.FMT);
    }

    /** Volumi senza decimali inutili: 700 e non 700.0. */
    public static String formatVolume(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }
        return String.format("%.1f", value);
    }

    /** Etichetta corta per l'asse del grafico ("02.02"). */
    public static String shortDate(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    /** Legge "HH:mm" (accetta anche "H:mm"); restituisce null se non valido. */
    public static LocalTime parseTime(String text) {
        String t = text == null ? "" : text.trim();
        for (String pattern : new String[]{"H:mm", "HH:mm"}) {
            try {
                return LocalTime.parse(t, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
                // Prova con il formato successivo.
            }
        }
        return null;
    }

    /** Etichetta rossa di errore sotto i form (errori ben visibili). */
    public static Label errorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: #c0392b;");
        label.setWrapText(true);
        return label;
    }

    public static void showError(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showWarning(String message) {
        show(Alert.AlertType.WARNING, "Warning", message);
    }

    public static void showInfo(String message) {
        show(Alert.AlertType.INFORMATION, "Information", message);
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /** Conferma "Sei sicuro...?" per ogni cancellazione annullabile (spec). */
    public static boolean confirmDelete(String what) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure that you want to delete " + what + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Delete");
        alert.setHeaderText(null);
        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.YES;
    }

    /** Conferma perdita modifiche non salvate quando si annulla (spec). */
    public static boolean confirmDiscard() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "You have unsaved changes. Discard them?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Discard changes?");
        alert.setHeaderText(null);
        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.YES;
    }

    /** Messaggio di sovrapposizione usato dall'editor dei workout. */
    public static String overlapText(WorkoutSummary clash) {
        return "Overlaps with workout '" + clash.getName() + "' ("
                + formatDateTime(clash.getStart()) + " - "
                + formatDateTime(clash.getEnd()) + ").";
    }
}
