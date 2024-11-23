package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Student;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.raiseAlert;
import static org.group3.csc325project.RegistrationApp.setRoot;
import static org.group3.csc325project.SessionManager.getLoggedInUsername;

/**
 * Controller studentschedule.fxml. Handles the logic behind allowing a student to view what courses they're registered for
 */
public class StudentScheduleController {
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
    @FXML
    private TableColumn<Course, String> columnCourseTextbook;
    //Currently selected course from the TableView
    private Course selectedCourse;

    /**
     * Runs when page is loaded, sets up columns and reads Firebase for what courses a student is registered for.
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
        columnCourseTextbook.setCellValueFactory(new PropertyValueFactory<>("courseTextbook"));

        handleReadFirebase();
    }

    /**
     * Helper method that reads Firebase for any courses a student is enrolled in
     */
    private void handleReadFirebase() {
        Firestore db = FirestoreClient.getFirestore();

        //Get information about the currently logged in Student
        ApiFuture<QuerySnapshot> studentQuery = db.collection("Student").whereEqualTo("Username", getLoggedInUsername()).get();

        try {
            QuerySnapshot studentSnapshot = studentQuery.get();

            if(!studentSnapshot.isEmpty()) {
                DocumentSnapshot studentDoc = studentSnapshot.getDocuments().get(0);

                Map<String, Map<String, Object>> enrolledCourses = (Map<String, Map<String, Object>>) studentDoc.get("EnrolledCourses");

                if(enrolledCourses != null && enrolledCourses.size() > 0) {
                    //Now get each course and stick it in the TableView
                    handleGetCourses(enrolledCourses.keySet());
                }

            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }


    } // end - handleReadFirebase

    /**
     * Helper method which will get all the courses a student is registered for from Firebase and add them to the TableView
     * @param crnSet set containing the CRNs passed in
     */
    private void handleGetCourses(Set<String> crnSet) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference coursesCollection = db.collection("Course");

        //Put the set in an ArrayList
        List<String> crnList = new ArrayList<>(crnSet);

        //Get any courses that have a matching CRN
        ApiFuture<QuerySnapshot> future = coursesCollection.whereIn("courseCRN", crnList).get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            //Now read through the documents and put the information in a course document
            //Just calls a helper method to do this
            for (QueryDocumentSnapshot document : documents) {
                documentToCourseInstance(document);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Helper method that converts a course document to a course object instance and adds it to the TableView
     */
    private void documentToCourseInstance(QueryDocumentSnapshot document) {
        try {
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
            course.setCourseTextbook(document.getString("courseTextbook"));
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

            //Now add the course to the TableView
            coursesTable.getItems().add(course);

        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    } // end - documentToCourseInstance

    /**
     * Method that handles dropping a selected course
     */
    public void handleDropCourseButton() {
        if(selectedCourse == null) {
            raiseAlert("Error: No course selected","Please select a course to drop");
            return;
        }
        System.out.printf("Drop course button pressed with course %s selected\n", selectedCourse.getCourseName());

        //Put user id into a student obj
        Student student = new Student();
        student.setUserId(getLoggedInUsername());
        student.setUsername(getLoggedInUsername());

        //WARNING: this currently does not work as intended, completely removes all courses a student is enrolled in
        //Temporarily commented out to avoid issues
        removeStudentFromCourse(selectedCourse, student);
    }

    /**
     * Helper method that handles removing the logged in student from a course
     * Taken from CoursesController with minimal changes
     * @param course Course to be dropped
     * @param student Student dropping the course
     */
    private void removeStudentFromCourse(Course course, Student student) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        try {
            boolean wasEnrolled = course.getEnrolledStudents().remove(student.getUserId());
            //Update counts based on whether the student was enrolled
            if (wasEnrolled) {
                course.decrementEnrolledCount();
            } else {
                //If the student was not found, display a message and return early
                showAlert("Student not found in course enrollment!");
                return;
            }
            CollectionReference coursesCollection = db.collection("Course");
            ApiFuture<QuerySnapshot> courseQuery = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
            List<QueryDocumentSnapshot> courseDocs = courseQuery.get().getDocuments();
            if (!courseDocs.isEmpty()) {
                DocumentReference courseRef = courseDocs.getFirst().getReference();
                //Update Firestore course document
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("currentEnrolledCount", course.getCurrentEnrolledCount());
                updatedData.put("enrolledStudents", course.getEnrolledStudents());
                courseRef.update(updatedData);
            }
            //Update the student document to remove the course from enrolled courses
            ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", student.getUserId()).get();
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
            if (!studentDocs.isEmpty()) {
                DocumentReference studentRef = studentDocs.getFirst().getReference();
                //Update Firestore student document
                Object existingEnrolledCourses = studentDocs.getFirst().get("EnrolledCourses");
                //This needs to be a Map and not a list
                //List<Map<String, Object>> enrolledCourses;
                Map<String, Map<String, Object>> enrolledCourses = (Map<String, Map<String, Object>>) existingEnrolledCourses;
                //Remove the course from enrolled courses in the student's document
                //enrolledCourses.removeIf(courseEntry -> course.getCourseCRN().equals(courseEntry.get("courseCRN")));
                enrolledCourses.remove(course.getCourseCRN());
                studentRef.update("EnrolledCourses", enrolledCourses);
            }
            showAlert("Student " + student.getUserId() + " has been removed from course " + course.getCourseCRN() + ".");
            coursesTable.refresh();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error removing student {} from course {}: ", student.getUserId(), course.getCourseCRN(), e);
        }
    }

    /**
     * Shows an alert with the given message when called
     * Taken from CoursesController
     * @author Yash
     * @param message Message to be displayed
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modify Course System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Method that switches scene to student.fxml
     */
    public void handleGoBackButton() {
        setRoot("student");
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

}
