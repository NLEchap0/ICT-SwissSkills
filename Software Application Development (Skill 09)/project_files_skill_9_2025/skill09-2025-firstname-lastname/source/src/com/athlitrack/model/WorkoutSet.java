package com.athlitrack.model;

/**
 * Singolo set di un esercizio all'interno di un workout.
 * Il numero del set parte da 1 e viene assegnato automaticamente.
 */
public class WorkoutSet {

    private int id;
    private int setNumber;
    private double weight;
    private int reps;

    public WorkoutSet() {
    }

    public WorkoutSet(int setNumber, double weight, int reps) {
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }
}