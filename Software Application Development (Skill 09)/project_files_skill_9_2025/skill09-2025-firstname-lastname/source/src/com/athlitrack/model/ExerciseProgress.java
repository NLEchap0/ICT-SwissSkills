package com.athlitrack.model;

import java.time.LocalDateTime;

/**
 * Esecuzione di un esercizio in un workout: data e volume totale (somma peso x reps).
 * Usata dalla finestra Progress per mostrare l'andamento nel tempo.
 */
public class ExerciseProgress {

    private LocalDateTime date;
    private double totalVolume;

    public ExerciseProgress(LocalDateTime date, double totalVolume) {
        this.date = date;
        this.totalVolume = totalVolume;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }
}