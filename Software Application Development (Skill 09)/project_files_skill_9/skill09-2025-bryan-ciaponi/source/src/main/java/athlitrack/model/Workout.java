package athlitrack.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Un workout completo: nome, inizio/fine e lista ordinata di esercizi.
 */
public class Workout {
    private int id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<WorkoutExercise> exercises = new ArrayList<>();

    public Workout(int id, String name, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public List<WorkoutExercise> getExercises() { return exercises; }

    public void setName(String name) { this.name = name; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public void setEnd(LocalDateTime end) { this.end = end; }
}
