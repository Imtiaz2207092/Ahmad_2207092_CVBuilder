package com.example.cv_builder;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    @FXML
    private TableView<CVModel> cvTableView;
    @FXML
    private TableColumn<CVModel, String> nameColumn;
    @FXML
    private TableColumn<CVModel, String> emailColumn;
    @FXML
    private TableColumn<CVModel, String> phoneColumn;
    @FXML
    private Button createButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button viewButton;
    @FXML
    private ProgressIndicator progressIndicator;

    private CVDAO cvDAO;
    private ObservableList<CVModel> cvList;

    @FXML
    public void initialize() {
        cvDAO = new CVDAO();
        cvList = javafx.collections.FXCollections.observableArrayList();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        cvTableView.setItems(cvList);

        loadCVs();

        cvTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
            viewButton.setDisable(!hasSelection);
        });
    }

    private void loadCVs() {
        progressIndicator.setVisible(true);
        createButton.setDisable(true);

        Service<ObservableList<CVModel>> service = new Service<ObservableList<CVModel>>() {
            @Override
            protected Task<ObservableList<CVModel>> createTask() {
                return cvDAO.getAllCVs();
            }
        };

        service.setOnSucceeded(e -> {
            ObservableList<CVModel> result = service.getValue();
            cvList.setAll(result);
            progressIndicator.setVisible(false);
            createButton.setDisable(false);
        });

        service.setOnFailed(e -> {
            progressIndicator.setVisible(false);
            createButton.setDisable(false);
            showError("Error loading CVs: " + service.getException().getMessage());
        });

        service.start();
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
            showError("Error opening create CV window: " + e.getMessage());
        }
    }

    @FXML
    private void editCV(ActionEvent event) {
        CVModel selectedCV = cvTableView.getSelectionModel().getSelectedItem();
        if (selectedCV == null) {
            showError("Please select a CV to edit.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CreateCV.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            CreateCVController controller = fxmlLoader.getController();
            controller.loadCVForEdit(selectedCV);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Edit CV");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening edit window: " + e.getMessage());
        }
    }

    @FXML
    private void deleteCV() {
        CVModel selectedCV = cvTableView.getSelectionModel().getSelectedItem();
        if (selectedCV == null) {
            showError("Please select a CV to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete CV");
        confirmAlert.setContentText("Are you sure you want to delete " + selectedCV.getName() + "'s CV?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                progressIndicator.setVisible(true);
                deleteButton.setDisable(true);

                Task<Boolean> deleteTask = cvDAO.deleteCV(selectedCV.getId());
                deleteTask.setOnSucceeded(e -> {
                    progressIndicator.setVisible(false);
                    deleteButton.setDisable(false);
                    if (deleteTask.getValue()) {
                        showInfo("CV deleted successfully!");
                        loadCVs();
                    } else {
                        showError("Failed to delete CV.");
                    }
                });
                deleteTask.setOnFailed(e -> {
                    progressIndicator.setVisible(false);
                    deleteButton.setDisable(false);
                    showError("Error deleting CV: " + deleteTask.getException().getMessage());
                });

                new Thread(deleteTask).start();
            }
        });
    }

    @FXML
    private void viewCV(ActionEvent event) {
        CVModel selectedCV = cvTableView.getSelectionModel().getSelectedItem();
        if (selectedCV == null) {
            showError("Please select a CV to view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Preview.fxml"));
            Scene scene = new Scene(loader.load());

            PreviewController controller = loader.getController();
            controller.loadData(selectedCV);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("CV Preview - " + selectedCV.getName());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening preview: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

