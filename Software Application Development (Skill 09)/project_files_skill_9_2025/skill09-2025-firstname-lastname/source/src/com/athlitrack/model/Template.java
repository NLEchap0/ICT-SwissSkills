package com.athlitrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Template di allenamento: contiene un nome e una lista ordinata di esercizi.
 */
public class Template {

    private int id;
    private String name;
    private List<TemplateItem> items = new ArrayList<>();

    public Template() {
    }

    public Template(int id, String name) {
        this.id = id;
        this.name = name;
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

    public List<TemplateItem> getItems() {
        return items;
    }

    public void setItems(List<TemplateItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return name;
    }
}