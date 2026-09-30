package athlitrack.model;

/**
 * Una singola serie svolta: peso sollevato (kg) per numero di ripetizioni.
 */
public class SetEntry {
    private double weight;
    private int reps;

    public SetEntry(double weight, int reps) {
        this.weight = weight;
        this.reps = reps;
    }

    public double getWeight() { return weight; }
    public int getReps() { return reps; }

    public void setWeight(double weight) { this.weight = weight; }
    public void setReps(int reps) { this.reps = reps; }

    /** Volume della serie: peso x ripetizioni (serve per le statistiche). */
    public double volume() { return weight * reps; }
}
