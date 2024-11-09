package org.group3.csc325project;

import course.Course;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import user.Professor;

/**
 * Controller for the courses.fxml file
 */
public class CoursesController {

    //TableView where Courses stored in Firebase are displayed
    @FXML
    private TableView<Course> coursesTable;

    //Column where the course CRN is displayed
    @FXML
    private TableColumn<Course,String> columnCrn;

    //Column where the course code is displayed
    @FXML
    private TableColumn<Course,String> columnCourseCode;

    //Column where the course name is displayed
    @FXML
    private TableColumn<Course,String> columnCourseName;

    //Column where the course instructor is displayed
    //Professor class will need a toString() override
    @FXML
    private TableColumn<Course,String> columnInstructor;

    //Column where the number of credits a course is worth is displayed
    @FXML
    private TableColumn<Course,String> columnCredits;

    //Column where the course meeting time is displayed
    @FXML
    private TableColumn<Course,String> columnCourseSchedule;

    //Column where the days a course meets is displayed
    @FXML
    private TableColumn<Course,String> columnCourseDays;

    //Column where the room a course takes place is displayed
    @FXML
    private TableColumn<Course,String> columnCourseLocation;

    //Column where the course capacity is displayed
    @FXML
    private TableColumn<Course,String> columnCourseCapacity;


    /**
     * Runs when page is loaded. Each column in the TableView is associated with a variable in the Course class
     */
    public void initialize() {
        columnCrn.setCellValueFactory(new PropertyValueFactory<Course,String>("courseCRN"));
        columnCourseCode.setCellValueFactory(new PropertyValueFactory<Course,String>("courseCode"));
        columnCourseName.setCellValueFactory(new PropertyValueFactory<Course,String>("courseName"));
        columnInstructor.setCellValueFactory(new PropertyValueFactory<Course,String>("professor"));
        columnCredits.setCellValueFactory(cellData -> {
            String returnString = String.format("%d", cellData.getValue().getCredits());
            return new SimpleStringProperty(returnString);
        });
        columnCourseSchedule.setCellValueFactory(new PropertyValueFactory<Course,String>("courseTime"));
        columnCourseDays.setCellValueFactory(new PropertyValueFactory<Course,String>("courseDays"));
        columnCourseLocation.setCellValueFactory(new PropertyValueFactory<Course,String>("courseLocation"));
        columnCourseCapacity.setCellValueFactory(cellData -> {
            int capacity = cellData.getValue().getCapacity();
            int enrolled = cellData.getValue().getCurrentEnrolledCount();
            String returnString = String.format("%d/%d", enrolled, capacity);
            return new SimpleStringProperty(returnString);
        });

        Course example = new Course();
        example.setCapacity(25);
        example.setCurrentEnrolledCount(5);
        example.setProfessor(new Professor());
        example.setCourseCRN("9999");
        example.setCourseCode("CSC999");
        example.setCourseName("Pearl Programming");
        example.setCourseTime("12:05-1:30pm");
        example.setCourseDays("Mon/Wed");
        example.setCourseLocation("WHIT 300");
        example.setCredits(9);
        coursesTable.getItems().add(example);

    }

}
