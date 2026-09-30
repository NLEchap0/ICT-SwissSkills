package athlitrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Un esercizio dentro un workout, con le serie effettivamente svolte.
 * La numerazione delle serie (1, 2, 3...) deriva dalla posizione in lista.
 */
public class WorkoutExercise {
    private int exerciseId;
    private String exerciseName;
    private List<SetEntry> sets = new ArrayList<>();

    public WorkoutExercise(int exerciseId, String exerciseName) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
    }

    public int getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public List<SetEntry> getSets() { return sets; }

    public void setExerciseId(int id) { this.exerciseId = id; }
    public void setExerciseName(String name) { this.exerciseName = name; }
}
