package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.raiseAlert;
import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for studentenroll.fxml. Handles the logic behind allowing a student to view and enroll in courses.
 */
public class StudentEnrollController {
    //Note: Much of the following code is largely the same as CoursesController
    //I've kept variable names largely the same across both StudentEnrollController and CoursesController

    //Just took this from CoursesController, feel free to change as you'd like Yash
    private static final Logger logger = LoggerFactory.getLogger(CoursesController.class);

    //TableView where course information is stored
    @FXML
    private TableView<Course> coursesTable;
    //Column where the course CRN is displayed
    @FXML
    private TableColumn<Course, String> columnCrn;
    //Column where the course code is displayed
    @FXML
    private TableColumn<Course, String> columnCourseCode;
    //Column where the course name is displayed
    @FXML
    private TableColumn<Course, String> columnCourseName;
    //Column where the course instructor is displayed
    @FXML
    private TableColumn<Course, String> columnInstructor;
    //Column where the number of credits a course is worth is displayed
    @FXML
    private TableColumn<Course, String> columnCredits;
    //Column where the course meeting time is displayed
    @FXML
    private TableColumn<Course, String> columnCourseSchedule;
    //Column where the days a course meets is displayed
    @FXML
    private TableColumn<Course, String> columnCourseDays;
    //Column where the room a course takes place is displayed
    @FXML
    private TableColumn<Course, String> columnCourseLocation;
    //Column where the course capacity is displayed
    @FXML
    private TableColumn<Course, String> columnCourseCapacity;
    @FXML
    private TableColumn<Course, String> columnCourseWaitlist;
    //TableView where Students enrolled in the selected course are displayed
    @FXML
    private TableView<Student> studentsTable;
    //Column where the student name is displayed
    @FXML
    private TableColumn<Student, String> columnStudentName;
    //Column where the student ID is displayed
    @FXML
    private TableColumn<Student, String> columnStudentID;
    //Currently selected course from the TableView
    private Course selectedCourse;

    /**
     * Runs when page is loaded. Each column in the TableView is associated with a variable in the Course class
     */
    public void initialize() {
        //Associate each column with a Course object variable
        columnCrn.setCellValueFactory(new PropertyValueFactory<>("courseCRN"));
        columnCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        columnCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        columnInstructor.setCellValueFactory(cellData -> cellData.getValue().professorNameProperty());
        columnCredits.setCellValueFactory(cellData -> {
            String returnString = String.format("%d", cellData.getValue().getCredits());
            return new SimpleStringProperty(returnString);
        });
        columnCourseSchedule.setCellValueFactory(new PropertyValueFactory<>("courseTime"));
        columnCourseDays.setCellValueFactory(new PropertyValueFactory<>("courseDays"));
        columnCourseLocation.setCellValueFactory(new PropertyValueFactory<>("courseLocation"));
        columnCourseCapacity.setCellValueFactory(cellData -> {
            int capacity = cellData.getValue().getCapacity();
            int enrolled = cellData.getValue().getCurrentEnrolledCount();
            String returnString = String.format("%d/%d", enrolled, capacity);
            return new SimpleStringProperty(returnString);
        });
        columnCourseWaitlist.setCellValueFactory(cellData -> {
            int currentWaitlistCount = cellData.getValue().getCurrentWaitlistCount();
            return new SimpleStringProperty(String.valueOf(currentWaitlistCount));
        });

        // Reads Course collection in the Firestore database and adds those courses to the TableView
        handleReadFirebase();

    }

    /**
     * Helper method which reads the Firestore course collection and adds those courses to the TableView
     */
    private void handleReadFirebase() {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference coursesCollection = db.collection("Course");
        ApiFuture<QuerySnapshot> future = coursesCollection.get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Course> courses = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Course course = document.toObject(Course.class);
                course.setCourseCRN(document.getString("courseCRN"));
                course.setCourseCode(document.getString("courseCode"));
                course.setCourseName(document.getString("courseName"));
                course.setCourseDays(document.getString("courseDays"));
                course.setCourseTime(document.getString("courseTime"));
                course.setCourseLocation(document.getString("courseLocation"));
                course.setCredits(document.getLong("credits").intValue());
                course.setCapacity(document.getLong("capacity").intValue());
                course.setCurrentEnrolledCount(document.getLong("currentEnrolledCount").intValue());
                // Get the professor reference
                DocumentReference professorRef = document.get("Professor", DocumentReference.class);
                if (professorRef != null) {
                    // Resolve professor reference to get the professor name
                    ApiFuture<DocumentSnapshot> professorFuture = professorRef.get();
                    DocumentSnapshot professorDocument = professorFuture.get();
                    if (professorDocument.exists()) {
                        String firstName = professorDocument.getString("FirstName");
                        String lastName = professorDocument.getString("LastName");
                        if (firstName != null && lastName != null) {
                            course.getProfessor().setFirstName(firstName);
                            course.getProfessor().setLastName(lastName);
                            String fullName = firstName + " " + lastName;
                            course.setProfessorName(fullName); // Set the full professor name
                        } else {
                            System.err.println("Warning: Missing first name or last name for professor with ID: " + professorRef.getId());
                        }
                    } else {
                        System.err.println("Warning: No professor found with ID: " + professorRef.getId());
                    }
                    // Set the professor reference in the course
                    course.setProfessorReference(professorRef);
                } else {
                    System.err.println("Warning: No professor reference found for course: " + course.getCourseCRN());
                }
                courses.add(course);
            }
            // Set items to the courses table
            coursesTable.getItems().setAll(courses);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the user selecting an item in the TableView
     * Updates the selectedItem variable
     * @param event mouse click event
     */
    public void handleCoursesTableViewMouseClick(MouseEvent event) {
        ObservableList<Course> courses = coursesTable.getItems();
        int selectedIndex = coursesTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < courses.size()) {
            //Sets selected course to what was clicked
            selectedCourse = courses.get(selectedIndex);
            logger.info("Selected course: {} - {}", selectedCourse.getCourseName(), selectedCourse.getProfessor());
        }
    }

    /**
     * When called registers the logged in student to the selected course
     */
    public void handleRegisterButton() {
        //Check if a course was actually selected
        if(selectedCourse == null) {
            raiseAlert("Error: Course not selected","Please select a course before attempting to register");
            return;
        }
        System.out.printf("Register button pressed, course %s is selected\n", selectedCourse.getCourseName());
    }

    /**
     * Method that switches scene to student.fxml
     */
    public void handleGoBackButton() {
        setRoot("student");
    }

}