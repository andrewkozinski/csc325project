package org.group3.csc325project;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for studentgrades.fxml
 */
public class StudentGradesController {

    /**
     * Method that switches scene to student.fxml
     */
    public void handleGoBackButton() {
        setRoot("student");
    }
}
