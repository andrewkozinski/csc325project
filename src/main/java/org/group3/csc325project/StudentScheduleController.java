package org.group3.csc325project;

import javafx.scene.input.MouseEvent;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller studentschedule.fxml. Handles the logic behind allowing a student to view what courses they're registered for
 */
public class StudentScheduleController {



    /**
     * Runs when page is loaded, sets up columns and reads Firebase for what courses a student is registered for.
     */
    public void initialize() {

    }

    /**
     * Method that handles dropping a selected course
     */
    public void handleDropCourseButton() {
        System.out.println("Drop course button pressed");
    }


    /**
     * Method that switches scene to student.fxml
     */
    public void handleGoBackButton() {
        setRoot("student");
    }

    public void handleCoursesTableViewMouseClick(MouseEvent event) {}

}
