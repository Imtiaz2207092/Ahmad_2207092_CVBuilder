package com.example.cv_builder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML
    private TableView<CVModel> cvTableView;
    @FXML
    private TableColumn<CVModel, String> nameColumn;
    @FXML
    private TableColumn<CVModel, String> emailColumn;
    @FXML
    private TableColumn<CVModel, String> phoneColumn;

    private CVDAO cvDAO;

    @FXML
    public void initialize() {
        cvDAO = new CVDAO();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadCVs();
    }

    private void loadCVs() {
        List<CVModel> cvList = cvDAO.getAllCVs();
        ObservableList<CVModel> observableList = FXCollections.observableArrayList(cvList);
        cvTableView.setItems(observableList);
    }

    @FXML
    private void openCreateCV(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CreateCV.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Create Your CV");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

