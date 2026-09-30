package athlitrack.model;

import java.time.LocalDateTime;

/**
 * Riga leggera per la tabella dei workout: niente serie caricate,
 * solo i dati che servono alla lista (nome, date, durata calcolata).
 */
public class WorkoutSummary {
    private final int id;
    private final String name;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public WorkoutSummary(int id, String name, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
}
