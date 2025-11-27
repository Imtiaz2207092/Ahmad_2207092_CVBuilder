package com.example.cv_builder;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateCVController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextArea educationField;
    @FXML private TextArea skillsField;
    @FXML private TextArea experienceField;
    @FXML private TextArea projectsField;
    @FXML private TextField photoPathField;
    @FXML private Button saveButton;
    @FXML private Button generateButton;
    @FXML private ProgressIndicator progressIndicator;

    private CVDAO cvDAO;
    private CVModel currentCV;
    private ExecutorService executorService;

    @FXML
    public void initialize() {
        cvDAO = new CVDAO();
        executorService = Executors.newCachedThreadPool();
        progressIndicator.setVisible(false);
    }

    public void loadCVForEdit(CVModel cv) {
        currentCV = cv;
        nameField.setText(cv.getName());
        emailField.setText(cv.getEmail());
        phoneField.setText(cv.getPhone());
        addressField.setText(cv.getAddress());
        educationField.setText(cv.getEducation());
        skillsField.setText(cv.getSkills());
        experienceField.setText(cv.getExperience());
        projectsField.setText(cv.getProjects());
        photoPathField.setText(cv.getPhotoPath());
    }

    @FXML
    private void choosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            photoPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void saveCV(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        CVModel model = createCVModelFromFields();
        progressIndicator.setVisible(true);
        saveButton.setDisable(true);
        generateButton.setDisable(true);

        Task<?> saveTask;
        if (currentCV != null) {
            model.setId(currentCV.getId());
            saveTask = cvDAO.updateCV(model);
            saveTask.setOnSucceeded(e -> {
                progressIndicator.setVisible(false);
                saveButton.setDisable(false);
                generateButton.setDisable(false);
                showInfo("CV updated successfully!");
                goBackToHome(event);
            });
        } else {
            saveTask = cvDAO.insertCV(model);
            saveTask.setOnSucceeded(e -> {
                progressIndicator.setVisible(false);
                saveButton.setDisable(false);
                generateButton.setDisable(false);
                Integer newId = (Integer) saveTask.getValue();
                model.setId(newId);
                showInfo("CV saved successfully!");
                goBackToHome(event);
            });
        }

        saveTask.setOnFailed(e -> {
            progressIndicator.setVisible(false);
            saveButton.setDisable(false);
            generateButton.setDisable(false);
            showError("Error saving CV: " + saveTask.getException().getMessage());
        });

        executorService.execute(saveTask);
    }

    @FXML
    private void generateCV(ActionEvent event) throws IOException {
        if (!validateInput()) {
            return;
        }

        CVModel model = createCVModelFromFields();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Preview.fxml"));
        Scene scene = new Scene(loader.load());

        PreviewController controller = loader.getController();
        controller.loadData(model);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void goBackToHome(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("CV Builder - Home");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to home: " + e.getMessage());
        }
    }

    private CVModel createCVModelFromFields() {
        CVModel model = new CVModel();
        model.setName(nameField.getText());
        model.setEmail(emailField.getText());
        model.setPhone(phoneField.getText());
        model.setAddress(addressField.getText());
        model.setEducation(educationField.getText());
        model.setSkills(skillsField.getText());
        model.setExperience(experienceField.getText());
        model.setProjects(projectsField.getText());
        model.setPhotoPath(photoPathField.getText());
        return model;
    }

    private boolean validateInput() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showError("Please enter your name.");
            return false;
        }
        return true;
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


