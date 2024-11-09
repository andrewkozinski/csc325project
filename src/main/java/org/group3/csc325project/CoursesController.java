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
import user.Professor;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.setRoot;

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

    //selectedCourse is the currently selected item from the TableView
    //Updated when user selects an item in the table view
    private Course selectedCourse;


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

        //Reads Course collection in the Firestore database and adds those courses to the TableView
        handleReadFirebase();
    }

    /**
     * Helper method which reads firestore course collection and adds courses to the tableview
     */
    private void handleReadFirebase() {
        Firestore db = FirestoreClient.getFirestore();

        //Get the course docs
        ApiFuture<QuerySnapshot> coursesFuture = db.collection("Course").get();
        List<QueryDocumentSnapshot> courseDocs;

        try {
            courseDocs = coursesFuture.get().getDocuments();

            //Loop through the list of course documents and add to courses tableview
            for(QueryDocumentSnapshot doc : courseDocs) {
                Course course = new Course();
                //Set course information
                course.setCourseCRN(doc.getString("courseCRN"));
                course.setCourseCode(doc.getString("courseCode"));
                course.setCourseName(doc.getString("courseName"));
                course.setCourseTime(doc.getString("courseTime"));
                course.setCourseDays(doc.getString("courseDays"));
                course.setCourseLocation(doc.getString("courseLocation"));
                course.setCredits(doc.get("credits", Integer.class));
                course.setCapacity(doc.get("capacity", Integer.class));
                course.setCurrentEnrolledCount(doc.get("currentEnrolledCount", Integer.class));

                //Now we need to get the professor reference and get the information about the professor
                Professor prof;
                DocumentReference professorRef = doc.get("Professor", DocumentReference.class);
                if(professorRef != null) {
                    prof = new Professor();

                    ApiFuture<DocumentSnapshot> professorFuture = professorRef.get();
                    DocumentSnapshot professor = professorFuture.get();
                    prof.setFirstName(professor.getString("FirstName"));
                    prof.setLastName(professor.getString("LastName"));
                    prof.setDepartment(professor.getString("Department"));
                    prof.setEmail(professor.getString("Email"));
                    prof.setUserName(professor.getString("UserName"));

                } // end if
                else {
                    prof = new Professor();
                    prof.setFirstName(doc.getString("TBA"));
                    prof.setLastName(doc.getString(""));
                } // end else
                course.setProfessor(prof);

                coursesTable.getItems().add(course);

            } // end for

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
        if(selectedIndex >= 0 && selectedIndex < courses.size()) {
            //Sets selected course to what was clicked
            selectedCourse = courses.get(selectedIndex);
            System.out.println(selectedCourse.getCourseName() + " - " + selectedCourse.getProfessor());
        }
    }


    /**
     * Method that's called upon the add course button being pressed
     * Currently, doesn't do anything aside from printing out a message
     */
    public void handleAddCourseButton() {
        System.out.println("add course button pressed");
    }

    /**
     * Method that's called upon the edit course button being pressed
     * Currently, doesn't do anything aside from printing out a message
     */
    public void handleEditCourseButton() {
        System.out.println("edit course button pressed");
    }

    /**
     * Method that's called upon the assign course button being pressed
     * Currently, doesn't do anything aside from printing out a message
     */
    public void assignCourseButton() {
        System.out.println("assign course button pressed");
    }

    /**
     * Simply sets the root back to the base admin area
     * This button is temporary
     */
    public void backButton() {
        setRoot("admin");
    }

}
