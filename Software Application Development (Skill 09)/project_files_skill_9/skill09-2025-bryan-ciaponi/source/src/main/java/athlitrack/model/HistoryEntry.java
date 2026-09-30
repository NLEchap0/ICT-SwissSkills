package athlitrack.model;

import java.time.LocalDateTime;

/**
 * Una esecuzione di un esercizio in una sessione di workout (vista Progress).
 * Se lo stesso giorno ci sono più workout, ognuno resta un record separato.
 */
public class HistoryEntry {
    private final int workoutId;
    private final String workoutName;
    private final LocalDateTime date;
    private final double volume;
    private final int sets;
    private final int reps;

    public HistoryEntry(int workoutId, String workoutName, LocalDateTime date,
                        double volume, int sets, int reps) {
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.date = date;
        this.volume = volume;
        this.sets = sets;
        this.reps = reps;
    }

    public int getWorkoutId() { return workoutId; }
    public String getWorkoutName() { return workoutName; }
    public LocalDateTime getDate() { return date; }
    public double getVolume() { return volume; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
}
