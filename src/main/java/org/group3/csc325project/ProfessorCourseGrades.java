package org.group3.csc325project;

import course.Course;
import course.Grade;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ProfessorCourseGrades {

    //Table to display student grades
    @FXML
    private TableView<Grade> gradesTable;
    //Column where student first name is displayed
    @FXML
    private TableColumn studentFirstNameColumn;
    //Column where student last name is displayed
    @FXML
    private TableColumn studentLastNameColumn;
    //Label for the current course
    @FXML
    private Label courseLabel;
    //The current course
    private Course currentCourse;

    /**
     * Runs when page is loaded
     */
    public void initialize() {

        //Gets the course from CoursesController
        //Note course should never be null since a null check is done before going to this page
        currentCourse = ProfessorCoursesController.getSelectedCourse();

        courseLabel.setText(String.format("Course grades for: %s (%s)", currentCourse.getCourseName(), currentCourse.getCourseCode()));


    }

}
