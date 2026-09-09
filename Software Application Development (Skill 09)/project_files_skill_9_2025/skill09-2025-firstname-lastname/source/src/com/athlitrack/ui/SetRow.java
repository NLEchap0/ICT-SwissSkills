package com.athlitrack.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Riga della tabella di modifica di un workout: un singolo set di un esercizio.
 *
 * Le righe "fantasma" sono righe vuote mostrate in fondo a ogni esercizio:
 * l'utente le compila per aggiungere un nuovo set (i numeri di set restano automatici).
 */
public class SetRow {

    private final StringProperty esercizio = new SimpleStringProperty();
    private final IntegerProperty numeroSet = new SimpleIntegerProperty(1);
    private final StringProperty peso = new SimpleStringProperty("");
    private final StringProperty reps = new SimpleStringProperty("");
    private final BooleanProperty fantasma = new SimpleBooleanProperty(false);

    public StringProperty esercizioProperty() {
        return esercizio;
    }

    public IntegerProperty numeroSetProperty() {
        return numeroSet;
    }

    public StringProperty pesoProperty() {
        return peso;
    }

    public StringProperty repsProperty() {
        return reps;
    }

    public BooleanProperty fantasmaProperty() {
        return fantasma;
    }

    public boolean isFantasma() {
        return fantasma.get();
    }
}