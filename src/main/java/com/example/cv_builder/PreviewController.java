package com.example.cv_builder;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PreviewController {

    @FXML private ImageView photoView;
    @FXML private Label nameText;
    @FXML private Label emailText;
    @FXML private Label phoneText;
    @FXML private Label addressText;
    @FXML private Label educationText;
    @FXML private Label skillsText;
    @FXML private Label experienceText;
    @FXML private Label projectsText;

    public void loadData(CVModel m) {
        nameText.setText("Name: " + m.getName());
        emailText.setText("Email: " + m.getEmail());
        phoneText.setText("Phone: " + m.getPhone());
        addressText.setText("Address: " + m.getAddress());
        educationText.setText(m.getEducation());
        skillsText.setText(m.getSkills());
        experienceText.setText(m.getExperience());
        projectsText.setText(m.getProjects());


        if (m.getPhotoPath() != null && !m.getPhotoPath().isEmpty()) {
            Image image = new Image("file:" + m.getPhotoPath());
            photoView.setImage(image);
        }
    }
}

