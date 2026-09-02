package com.example.gestioneutenti.controller;

import com.example.gestioneutenti.db.Connect;
import com.example.gestioneutenti.db.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.transform.Result;
import java.sql.*;

public class userController {
    @FXML
    public void initialize() throws SQLException {
        colName.setCellValueFactory(cellData -> cellData.getValue().getName());
        colSurname.setCellValueFactory(cellData -> cellData.getValue().getSurname());
        refresh();
    }

    public Label statusMessage;
    public TextField nameField;
    public TextField surnameField;

    @FXML
    protected void onButtonClickAddUser() throws SQLException {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        if (name.isEmpty() || surname.isEmpty()) {
            statusMessage.setStyle("-fx-text-fill: red;");
            statusMessage.setText("Mandatory fields! Insert data and try again.");
            return;
        }

        Connect connect = new Connect();
        Connection c = connect.getConnection();

        if(c != null) {
            String query = "INSERT INTO user(name, surname) values (?, ?);";
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setString(1, nameField.getText());
            preparedStatement.setString(2, surnameField.getText());
            preparedStatement.execute();
            c.close();

            refresh();

            nameField.setText("");
            surnameField.setText("");
            statusMessage.setStyle("-fx-text-fill: green;");
            statusMessage.setText("User saved successfully!");
        }else{
            statusMessage.setStyle("-fx-text-fill: red;");
            statusMessage.setText("Something went wrong! Try again.");
        }
    }





    @FXML public TableView<User> userTable;
    @FXML public TableColumn<User, String> colName;
    @FXML public TableColumn<User, String> colSurname;

    @FXML
    public void refresh() throws SQLException {
        userTable.setItems(loadUsers());
    }

    @FXML
    protected ObservableList<User> loadUsers() throws SQLException {
        ObservableList<User> returnList = FXCollections.observableArrayList();

        Connect connect = new Connect();
        Connection c = connect.getConnection();

        if (c != null) {
            String query = "SELECT * FROM user;";
            PreparedStatement ps = c.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                returnList.add(new User(
                        rs.getString("name"),
                        rs.getString("surname")
                ));
            }
            c.close();
        }
        return returnList;
    }
}