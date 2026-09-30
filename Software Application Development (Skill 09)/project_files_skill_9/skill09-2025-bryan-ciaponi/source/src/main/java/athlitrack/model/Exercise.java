package athlitrack.model;

/**
 * Un esercizio del database (es. "Barbell Squat").
 * Classe dati semplice: contiene solo i campi della tabella exercises.
 * Tutta la logica SQL sta invece in Database.
 */
public class Exercise {
    private int id;
    private String name;
    private String description;
    private String type;

    public Exercise(int id, String name, String description, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }

    // JavaFX usa toString() per mostrare gli oggetti nei ComboBox: mostriamo il nome.
    @Override
    public String toString() { return name; }
}
