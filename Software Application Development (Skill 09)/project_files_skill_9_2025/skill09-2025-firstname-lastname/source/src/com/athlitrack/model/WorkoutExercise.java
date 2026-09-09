package com.athlitrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Esercizio all'interno di un workout, con la lista dei suoi set.
 */
public class WorkoutExercise {

    private int id;
    private Exercise exercise;
    private int position;
    private List<WorkoutSet> sets = new ArrayList<>();

    public WorkoutExercise() {
    }

    public WorkoutExercise(Exercise exercise, int position) {
        this.exercise = exercise;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = sets;
    }
}