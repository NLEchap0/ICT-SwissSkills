package com.example.gestioneutenti.db;

import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

@Data
public class User {
    int id;
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty surname = new SimpleStringProperty();

    public User(String name, String surname) {
        this.name.set(name);
        this.surname.set(surname);
    }
}
