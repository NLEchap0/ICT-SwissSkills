package com.example.gestioneutenti.db;

import com.google.protobuf.Empty;

import java.lang.reflect.GenericDeclaration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    public Connect() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/javafx",
                    "root",
                    ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/javafx",
                    "root",
                    ""
            );
        } catch (SQLException e) {
            return null;
        }
    }
}
