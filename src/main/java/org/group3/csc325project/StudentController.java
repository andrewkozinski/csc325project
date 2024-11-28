package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for student.fxml, handles user interactions and updating the UI based on student actions.
 */
public class StudentController {
    @FXML
    public VBox studentVbox;
    @FXML
    public AnchorPane studentAnchorPane;
    @FXML
    public ImageView studentSideBackground;
    @FXML
    public ImageView studentHeader;
    @FXML
    public ImageView studentCoursesButton;
    @FXML
    public ImageView studentGradesButton;
    @FXML
    public ImageView studentSchedule;
    @FXML
    public HBox topHBox;

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
