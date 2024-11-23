package org.group3.csc325project;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for student.fxml, handles user interactions and updating the UI based on student actions.
 */
public class StudentController {

    /**
     * Upon call, switches scene back to login.fxml.
     */
    public void returnToLogin() {
        RegistrationApp.returnToLogin();
    }

    /**
     * Upon call, switches scene to studentenroll.fxml
     */
    public void goToEnrollPage() {
        setRoot("studentenroll");
    }

    /**
     * Upon call, switches scene to studentschedule.fxml
     */
    public void goToSchedulePage() {
        setRoot("studentschedule");
    }

    /**
     * Upon call, switches scene to studentgrades.fxml
     */
    public void goToGradesPage() {
        setRoot("studentgrades");
    }

}
