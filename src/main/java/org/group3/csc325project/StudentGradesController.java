package org.group3.csc325project;

import course.Grade;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for studentgrades.fxml
 */
public class StudentGradesController {

    //TableView where grades will be stored
    //Uses same Grade object that ProfessorCoursesGrades uses
    @FXML
    private TableView<Grade> gradesTable;
    //Column where course CRN is displayed
    @FXML
    private TableColumn<Grade, String> courseCrnColumn;
    //Column where a course name is displayed
    @FXML
    private TableColumn<Grade, String> courseNameColumn;
    //Column where a student's grade is displayed
    @FXML
    private TableColumn<Grade, Double> studentGradeColumn;

    /**
     * Runs when page is loaded, sets up columns and calls helper method to read Firebase for grades
     */
    public void initialize() {

        courseCrnColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getCourse().getCourseCRN());
        });
        courseNameColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getCourse().getCourseName());
        });
        studentGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));



    }


    /**
     * Method that switches scene to student.fxml
     */
    public void handleGoBackButton() {
        setRoot("student");
    }
}
