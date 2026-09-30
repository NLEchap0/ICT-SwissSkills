package athlitrack.model;

/**
 * Una riga di esercizio dentro un template: "sets" serie da "reps" ripetizioni.
 * L'ordine degli elementi nella lista del template conta e viene preservato.
 */
public class TemplateItem {
    private int exerciseId;
    private String exerciseName;
    private int sets;
    private int reps;

    public TemplateItem(int exerciseId, String exerciseName, int sets, int reps) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
    }

    public int getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }

    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setSets(int sets) { this.sets = sets; }
    public void setReps(int reps) { this.reps = reps; }
}
