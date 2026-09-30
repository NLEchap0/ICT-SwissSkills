package athlitrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Un template di allenamento: lista ordinata di esercizi.
 * Serve a pre-compilare i workout (serie/ripetizioni) quando si crea un workout.
 */
public class Template {
    private int id;
    private String name;
    private List<TemplateItem> items = new ArrayList<>();

    public Template(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<TemplateItem> getItems() { return items; }

    public void setName(String name) { this.name = name; }
    public void setItems(List<TemplateItem> items) { this.items = items; }

    @Override
    public String toString() { return name; }
}
