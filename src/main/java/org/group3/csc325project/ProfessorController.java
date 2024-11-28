package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for professor.fxml, handles user interactions and updating the UI based on professor actions.
 */
public class ProfessorController {
    @FXML
    public ImageView professorCourseButton;
    @FXML
    public VBox professorVbox;
    @FXML
    public AnchorPane professorAnchorPane;
    @FXML
    public ImageView professorSideBackground;
    @FXML
    public ImageView professorHeader;
    @FXML
    public HBox topHBox;


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
