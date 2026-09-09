package com.athlitrack.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Allenamento (workout): nome, data di inizio e fine e lista ordinata degli esercizi.
 */
public class Workout {

    private int id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<WorkoutExercise> exercises = new ArrayList<>();

    public Workout() {
    }

    public Workout(int id, String name, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }
}