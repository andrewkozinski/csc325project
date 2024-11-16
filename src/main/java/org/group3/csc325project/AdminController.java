package org.group3.csc325project;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for admin.fxml, handles user interactions and updating the UI based on admin actions.
 */
public class AdminController {

    /**
     * Upon call, switches scene back to login.fxml.
     */
    public void returnToLogin() {
        RegistrationApp.returnToLogin();
    }
    public void returnToCreateUser() {
        RegistrationApp.returnToCreateUser();
    }

    /**
     * Goes to accounts.fxml file where a mock tableview has been set up.
     */
    public void goToAccountsView() { setRoot("accounts"); }

    /**
     * Goes to courses.fxml file where a tableview displaying courses will be displayed
     */
    public void goToCoursesView() { setRoot("courses"); }
}
