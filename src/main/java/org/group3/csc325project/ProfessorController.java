package org.group3.csc325project;

/**
 * Controller for professor.fxml, handles user interactions and updating the UI based on professor actions.
 */
public class ProfessorController {

    /**
     * Upon call, switches scene back to login.fxml.
     */
    public void returnToLogin() {
        RegistrationApp.returnToLogin();
    }

    /**
     * Upon call, switches scene to view courses a Professor is assigned to
     */
    public void goToCoursesView() {
        RegistrationApp.setRoot("professorcourses");
    }

}
