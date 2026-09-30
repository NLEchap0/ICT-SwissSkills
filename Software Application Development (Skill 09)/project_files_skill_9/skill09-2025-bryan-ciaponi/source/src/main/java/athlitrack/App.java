package athlitrack;

import athlitrack.controller.MainController;
import athlitrack.controller.ExercisesController;
import athlitrack.controller.TemplatesController;
import athlitrack.controller.TemplateEditorController;
import athlitrack.controller.WorkoutEditorController;
import athlitrack.controller.WorkoutDetailController;
import athlitrack.controller.ProgressController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Finestra principale di AthliTrack (JavaFX): menu in alto, pagine FXML sotto.
 * Le viste sono definite in src/main/resources/athlitrack/view/*.fxml e
 * vengono caricate al bisogno; il comportamento sta in athlitrack.controller.*.
 * Una sola connessione Database condivisa per tutta l'app (semplice, qui basta).
 */
public class App extends Application {

    /** Una pagina FXML caricata: il nodo radice più il suo controller. */
    public record Loaded<T>(Parent root, T controller) { }

    /** Carica una pagina FXML dalle risorse (errore se il file manca o è rotto). */
    public static <T> Loaded<T> loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/athlitrack/view/" + fxmlFile));
            Parent root = loader.load();
            return new Loaded<>(root, loader.getController());
        } catch (IOException | NullPointerException e) {
            throw new DbException("Could not load view " + fxmlFile, e);
        }
    }

    private Stage stage;
    private BorderPane root;
    private Database db;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Database portabile: file accanto alla cartella di lavoro,
        // creato e riempito da exercises.json al primo avvio.
        db = new Database(new File("athlitrack.db"));
        try (InputStream json = App.class.getResourceAsStream("/exercises.json")) {
            if (json == null) {
                throw new DbException("Bundled exercises.json not found.");
            }
            db.importExercisesIfEmpty(json);
        } catch (DbException e) {
            UiHelpers.showError("Could not initialise the database:\n" + e.getMessage());
            throw e;
        } catch (Exception e) {
            UiHelpers.showError("Could not initialise the database:\n" + e.getMessage());
            throw new DbException("Startup failed.", e);
        }

        root = new BorderPane();
        root.setTop(buildMenu());

        Scene scene = new Scene(root, 1024, 680);
        primaryStage.setTitle("AthliTrack Gym Tracker");
        primaryStage.setMinWidth(920); // dimensione minima ragionevole (spec)
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        showMain();
        primaryStage.show();
    }

    private MenuBar buildMenu() {
        MenuItem workouts = new MenuItem("Workouts");
        workouts.setOnAction(e -> showMain());
        MenuItem exercises = new MenuItem("Exercises");
        exercises.setOnAction(e -> showExercises());
        MenuItem templates = new MenuItem("Templates");
        templates.setOnAction(e -> showTemplates());
        Menu navigate = new Menu("Navigate", null, workouts, exercises, templates);
        return new MenuBar(navigate);
    }

    public Database getDb() { return db; }
    public Stage getStage() { return stage; }

    // ---------------------------------------------------------- navigazione -
    // Ogni pagina viene ricaricata da FXML a ogni apertura (economico, sempre aggiornata).

    public void showMain() {
        Loaded<MainController> page = loadView("MainView.fxml");
        page.controller().setApp(this);
        root.setCenter(page.root());
    }

    public void showExercises() {
        Loaded<ExercisesController> page = loadView("ExercisesView.fxml");
        page.controller().setApp(this);
        root.setCenter(page.root());
    }

    public void showTemplates() {
        Loaded<TemplatesController> page = loadView("TemplatesView.fxml");
        page.controller().setApp(this);
        root.setCenter(page.root());
    }

    public void showTemplateEditor(Integer templateId) {
        Loaded<TemplateEditorController> page = loadView("TemplateEditorView.fxml");
        page.controller().setApp(this);
        page.controller().refresh(templateId);
        root.setCenter(page.root());
    }

    public void showWorkoutEditor(Integer workoutId) {
        Loaded<WorkoutEditorController> page = loadView("WorkoutEditorView.fxml");
        page.controller().setApp(this);
        page.controller().refresh(workoutId);
        root.setCenter(page.root());
    }

    public void showWorkoutDetail(int workoutId) {
        Loaded<WorkoutDetailController> page = loadView("WorkoutDetailView.fxml");
        page.controller().setApp(this);
        page.controller().refresh(workoutId);
        root.setCenter(page.root());
    }

    /** back: dove torna il pulsante Back ("Exercises" oppure "Workout:<id>"). */
    public void showProgress(int exerciseId, String back) {
        Loaded<ProgressController> page = loadView("ProgressView.fxml");
        page.controller().setApp(this);
        page.controller().refresh(exerciseId, back);
        root.setCenter(page.root());
    }
}
