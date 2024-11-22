package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import course.Grade;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import user.Student;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfessorCourseGrades {

    //Table to display student grades
    @FXML
    private TableView<Grade> gradesTable;
    //Column where student first name is displayed
    @FXML
    private TableColumn<Grade, String> studentFirstNameColumn;
    //Column where student last name is displayed
    @FXML
    private TableColumn<Grade, String> studentLastNameColumn;
    @FXML
    private TableColumn<Grade, String> studentGradeColumn;
    //Label for the current course
    @FXML
    private Label courseLabel;
    //The current course
    private Course currentCourse;
    //The selected Grade from the table
    private Grade selectedGrade;

    /**
     * Runs when page is loaded
     */
    public void initialize() {

        //Gets the course from CoursesController
        //Note: course should never be null since a null check is done before going to this page
        currentCourse = ProfessorCoursesController.getSelectedCourse();

        //This label can be removed later, made it for testing purposes
        courseLabel.setText(String.format("Course grades for: %s (%s)", currentCourse.getCourseName(), currentCourse.getCourseCode()));

        //Now associate each column with something in a Grade obj
        studentFirstNameColumn.setCellValueFactory(cellData -> {
            //Get student firstName out of the Student object stored in a Grade and put that String into a new SimpleStringProperty instance
            return new SimpleStringProperty(cellData.getValue().getStudent().getFirstName());
        });
        studentLastNameColumn.setCellValueFactory(cellData -> {
            //Get student lastName out of the Student object stored in a Grade and put that String into a new SimpleStringProperty instance
            return new SimpleStringProperty(cellData.getValue().getStudent().getLastName());
        });
        studentGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        //Test case, remove later
        Student stuartDent = new Student();
        stuartDent.setFirstName("Stuart");
        stuartDent.setLastName("Dent");
        Grade test = new Grade();
        test.setStudent(stuartDent);
        test.setCourse(currentCourse);
        test.setGrade(95.45);
        gradesTable.getItems().add(test);

        handleReadFirebase();
    }

    /**
     * Helper method that will read Firebase for all grades stored
     */
    private void handleReadFirebase() {

        //The Course collection in Firebase itself doesn't keep track of what Students are registered
        //Instead we have to read through the Student collection and see which Students registered

        for(String stuart: currentCourse.getEnrolledStudents()) {
            System.out.println(stuart);
        }


    }

}
