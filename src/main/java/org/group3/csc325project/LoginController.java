package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for login.fxml
 * All methods related to the login should likely go here.
 * Note: May be a good idea to rename this file to something other than "LoginController.java", not the best description
 */
public class LoginController {
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
        RegistrationApp.setRoot("student");
    }

    /**
     * On call, switches scene to professor.fxml
     */
    public void switchToProfessor() {
        RegistrationApp.setRoot("professor");
    }

    /**
     * On call, switches scene to admin.fxml
     */
    public void switchToAdmin() {
        RegistrationApp.setRoot("admin");
    }

}