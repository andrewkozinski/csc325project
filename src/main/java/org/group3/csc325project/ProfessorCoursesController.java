package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import user.Professor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.SessionManager.getLoggedInUsername;

/**
 * Controller which handles the code behind displaying the courses a Professor is assigned to
 */
public class ProfessorCoursesController {

    //TableView where Courses stored in Firebase are displayed
    @FXML
    private TableView<Course> assignedCoursesTable;

    //Column where the course CRN is displayed
    @FXML
    private TableColumn<Course,String> columnCrn;

    //Column where the course code is displayed
    @FXML
    private TableColumn<Course,String> columnCourseCode;

    //Column where the course name is displayed
    @FXML
    private TableColumn<Course,String> columnCourseName;

    //Column where the course description is displayed
    @FXML
    private TableColumn<Course,String> columnCourseDescription;

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
     * Runs when page is loaded. Gets courses a Professor is assigned to in the DB and adds them to a TableView on screen.
     */
    public void initialize() {

        //Following code involving the columns is largely the same to CoursesController with some minor differences
        columnCrn.setCellValueFactory(new PropertyValueFactory<Course,String>("courseCRN"));
        columnCourseCode.setCellValueFactory(new PropertyValueFactory<Course,String>("courseCode"));
        columnCourseName.setCellValueFactory(new PropertyValueFactory<Course,String>("courseName"));
        columnCourseDescription.setCellValueFactory(new PropertyValueFactory<Course,String>("courseDescription"));
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

        //This method call will get all the courses the logged in professor is assigned to in Firebase and retrieve them
        List<Course> courses = getAssignedCourses();

        //Add each course to the tableview
        for(Course course : courses) {
            assignedCoursesTable.getItems().add(course);
        }

    }

    /**
     * Helper method to retrieve the assigned courses
     * @return A list of Course objects
     */
    private List<Course> getAssignedCourses() {
        //ArrayList to be returned
        List<Course> courses = new ArrayList<>();

        Firestore db = FirestoreClient.getFirestore();

        //Get information about the currently logged in Professor
        ApiFuture<QuerySnapshot> professorQuery = db.collection("Professor").whereEqualTo("Username", getLoggedInUsername()).get();

        try {
            QuerySnapshot professorSnapshot = professorQuery.get();
            if(!professorSnapshot.isEmpty()) {
                //snapshot is not empty, so we found the professor we're looking for
                //The list should only have one item (since every username should be unique), so just get the professor at first index
                DocumentSnapshot professor = professorSnapshot.getDocuments().get(0);
                List<String> courseCrns = (List<String>) professor.get("AssignedCourses");
                if (courseCrns != null) {
                    //For each course name, we're going to get the course from the DB, stick its information in
                    //a course object, then add it to the list
                    for (String courseName : courseCrns) {
                        //Call helper method to get a course from the DB
                        Course course = getCourseByCrn(courseName);
                        if(course != null) {
                            courses.add(course);
                        }
                    }//end for
                }//end if
                else {
                    raiseAlert("Courses field not found", "Courses field returned null on the currently signed in Professor account.");
                }//end else

            }//end if
            else {
                //Searching through the Professor collection, we did not find a professor with the given id
                //So, just display an alert
                raiseAlert("User not found", "The id of the currently logged in user was not found in the database.");
            }//end else
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        //Now finally return the list of courses
        return courses;
    }

    /**
     * Helper method which searches the DB for a course with a given CRN, gets that course and creates a new Course object with the given course information inside.
     * @param crn The given CRN
     * @return A Course instance
     */
    private Course getCourseByCrn(String crn) {
        //Course to be returned
        Course returnCourse = null;
        Firestore db = FirestoreClient.getFirestore();

        //Find a course with the given CRN in the DB
        ApiFuture<QuerySnapshot> courseQuery = db.collection("Course").whereEqualTo("courseCRN", crn).get();

        try {
            QuerySnapshot courseSnapshot = courseQuery.get();
            if(!courseSnapshot.isEmpty()) {
                DocumentSnapshot course = courseSnapshot.getDocuments().get(0);

                //Now, put all the information into a new Course object
                returnCourse = new Course();
                returnCourse.setCourseCRN(crn);
                returnCourse.setCourseName(course.getString("courseName"));
                returnCourse.setCourseDescription(course.getString("courseDescription"));
                returnCourse.setCourseDays(course.getString("courseDays"));
                returnCourse.setCourseTime(course.getString("courseTime"));
                returnCourse.setCourseCode(course.getString("courseCode"));
                returnCourse.setCourseLocation(course.getString("courseLocation"));
                returnCourse.setCredits(course.get("credits", Integer.class));
                returnCourse.setCapacity(course.get("capacity", Integer.class));
                returnCourse.setCurrentEnrolledCount(course.get("currentEnrolledCount", Integer.class));
                returnCourse.setCourseTextbook(course.getString("requiredTextbook"));
            }//end if
            else {
                //No courses found with the given crn
                raiseAlert("Course not found", String.format("Course with given CRN '%s' was not found in the database.", crn));
            }//end else
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        //Now finally, return course instance (null if not found in DB)
        return returnCourse;
    }

    /**
     * Handles the user selecting an item in the TableView
     * Updates the selectedItem variable
     * @param event mouse click event
     */
    public void handleAssignedCoursesTableViewMouseClick(MouseEvent event) {
        ObservableList<Course> courses = assignedCoursesTable.getItems();
        int selectedIndex = assignedCoursesTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0 && selectedIndex < courses.size()) {
            //Sets selected course to what was clicked
            selectedCourse = courses.get(selectedIndex);
            System.out.println(selectedCourse.getCourseName() + " - CRN:" + selectedCourse.getCourseCRN());
        }
    }

    /**
     * Method that when called, displays an alert to the user
     * @param title Title to be displayed in the alert
     * @param message Messaged to be displayed in the alert
     */
    private void raiseAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
