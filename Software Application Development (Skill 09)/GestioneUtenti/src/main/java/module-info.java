module com.example.gestioneutenti {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires protobuf.java;


    opens com.example.gestioneutenti to javafx.fxml;
    exports com.example.gestioneutenti;
    exports com.example.gestioneutenti.db;
    opens com.example.gestioneutenti.db to javafx.fxml;
    exports com.example.gestioneutenti.controller;
    opens com.example.gestioneutenti.controller to javafx.fxml;
}