package com.athlitrack.ui;

import com.athlitrack.model.Exercise;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Riga modificabile della tabella di un template.
 * Avvolge un esercizio con numero di set e ripetizioni usando proprieta' JavaFX
 * per permettere la modifica inline nella tabella.
 */
public class TemplateItemRow {

    private final ObjectProperty<Exercise> esercizio = new SimpleObjectProperty<>();
    private final IntegerProperty sets = new SimpleIntegerProperty(1);
    private final IntegerProperty reps = new SimpleIntegerProperty(1);

    public ObjectProperty<Exercise> esercizioProperty() {
        return esercizio;
    }

    public IntegerProperty setsProperty() {
        return sets;
    }

    public IntegerProperty repsProperty() {
        return reps;
    }
}