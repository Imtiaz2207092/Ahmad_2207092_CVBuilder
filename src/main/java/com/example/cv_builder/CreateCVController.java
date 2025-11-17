package com.example.cv_builder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

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


    @FXML
    private void choosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            photoPathField.setText(selectedFile.getAbsolutePath());
        }
    }


    @FXML
    private void generateCV(ActionEvent event) throws IOException {
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Preview.fxml"));
        Scene scene = new Scene(loader.load());

        PreviewController controller = loader.getController();
        controller.loadData(model);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}


