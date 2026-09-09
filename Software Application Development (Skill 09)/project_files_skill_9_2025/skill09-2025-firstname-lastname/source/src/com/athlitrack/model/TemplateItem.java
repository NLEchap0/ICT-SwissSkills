package com.athlitrack.model;

/**
 * Esercizio all'interno di un template, con il numero di set e di ripetizioni.
 * La posizione preserva l'ordine degli esercizi nel template.
 */
public class TemplateItem {

    private int id;
    private Exercise exercise;
    private int position;
    private int sets;
    private int reps;

    public TemplateItem() {
    }

    public TemplateItem(Exercise exercise, int position, int sets, int reps) {
        this.exercise = exercise;
        this.position = position;
        this.sets = sets;
        this.reps = reps;
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

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }
}