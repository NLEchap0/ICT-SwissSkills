package athlitrack.controller;

import athlitrack.App;
import athlitrack.Database;
import athlitrack.DbException;
import athlitrack.UiHelpers;
import athlitrack.model.Exercise;
import athlitrack.model.HistoryEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * "Progressi di ...": ogni esecuzione (data + volume totale), il prossimo
 * volume atteso e un grafico con gli ultimi 3 mesi incluso il valore atteso.
 */
public class ProgressController {

    /** Riga piatta per la tabella dello storico. */
    public static class HistoryRow {
        private final String date;
        private final String volume;

        HistoryRow(String date, String volume) {
            this.date = date;
            this.volume = volume;
        }

        public String getDate() { return date; }
        public String getVolume() { return volume; }
    }

    @FXML private Label title;
    @FXML private TableView<HistoryRow> table;
    @FXML private Label info;
    @FXML private LineChart<String, Number> chart;

    private App app;
    private String back = "Exercises";

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    public void refresh(int exerciseId, String back) {
        this.back = back;
        Exercise exercise;
        List<HistoryEntry> history;
        try {
            exercise = app.getDb().getExercise(exerciseId);
            history = app.getDb().exerciseHistory(exerciseId);
        } catch (DbException e) {
            UiHelpers.showError("Could not load progress:\n" + e.getMessage());
            return;
        }
        title.setText("Progress of " + exercise.getName());

        // La lista usa i dati di sempre (spec).
        List<HistoryRow> rows = new ArrayList<>();
        for (HistoryEntry h : history) {
            rows.add(new HistoryRow(UiHelpers.formatDateTime(h.getDate()),
                    UiHelpers.formatVolume(h.getVolume())));
        }
        table.setItems(FXCollections.observableArrayList(rows));

        double expected = Database.expectedNextVolume(history);
        if (Double.isNaN(expected)) {
            info.setText("No records yet for this exercise. "
                    + "Complete a workout containing it to see progress.");
        } else {
            info.setText("Next expected total volume: "
                    + UiHelpers.formatVolume(expected) + " kg "
                    + "(average increase applied to the latest value).");
        }
        drawChart(history, expected);
    }

    /** Il grafico mostra solo gli ultimi 3 mesi (spec). */
    private void drawChart(List<HistoryEntry> history, double expected) {
        chart.getData().clear();
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(3);
        List<HistoryEntry> recent = new ArrayList<>();
        for (HistoryEntry h : history) {
            if (!h.getDate().isBefore(cutoff)) {
                recent.add(h);
            }
        }
        if (recent.isEmpty()) {
            return; // grafico vuoto con assi; l'etichetta info spiega il perché
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Volume");
        for (HistoryEntry h : recent) {
            series.getData().add(new XYChart.Data<>(
                    UiHelpers.shortDate(h.getDate()), h.getVolume()));
        }
        if (!Double.isNaN(expected)) {
            series.getData().add(new XYChart.Data<>("Expected", expected));
        }
        chart.getData().add(series);
    }

    @FXML
    void onBack() {
        if (back.startsWith("Workout:")) {
            app.showWorkoutDetail(Integer.parseInt(back.substring("Workout:".length())));
        } else {
            app.showExercises();
        }
    }
}
