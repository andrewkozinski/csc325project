package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controller for login.fxml
 * All methods related to the login should likely go here.
 * Note: May be a good idea to rename this file to something other than "HelloController.java", not the best description
 */
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application! - test push");
    }

    /**
     * On call, switches scene to student.fxml
     */
    public void switchToStudent() {
        try {
            HelloApplication.setRoot("student");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * On call, switches scene to professor.fxml
     */
    public void switchToProfessor() {
        try {
            HelloApplication.setRoot("professor");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * On call, switches scene to admin.fxml
     */
    public void switchToAdmin() {
        try {
            HelloApplication.setRoot("admin");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}