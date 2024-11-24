package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


        // Set custom TableRow factory
        coursesTable.setRowFactory(tv -> new TableRow<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (course == null || empty) {
                    setDisable(false);
                } else {
                    String studentUserId = SessionManager.getLoggedInUsername();
                    setDisable(isStudentEnrolledInCourse(course, studentUserId));
                }
            }
        });

        // Reads Course collection in the Firestore database and adds those courses to the TableView
        handleReadFirebase();

    }

    /**
     * Helper method that checks if a student is enrolled in a course or not
     * If a student is enrolled in a course, then the course will be disabled in the TableView
     * @param course Course passed in
     * @param studentUserId Student user id
     * @return True if enrolled in course otherwise false
     */
    private boolean isStudentEnrolledInCourse(Course course, String studentUserId) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", studentUserId).get();
        try {
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
            if (!studentDocs.isEmpty()) {
                DocumentSnapshot studentSnapshot = studentDocs.getFirst();
                Map<String, Object> enrolledCourses = (Map<String, Object>) studentSnapshot.get("EnrolledCourses");
                return enrolledCourses != null && enrolledCourses.containsKey(course.getCourseCRN());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return false;
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

        if (selectedCourse.getCurrentEnrolledCount() < selectedCourse.getCapacity()) {
            //If there is space, enroll the student directly
            assignStudentToCourse(selectedCourse, SessionManager.getLoggedInUsername());
        } else {
            //If the course is at capacity, assign to waitlist instead
            assignStudentToWaitlist(selectedCourse, SessionManager.getLoggedInUsername());
        }

    }

    /**
     * Helper method that registers the logged-in user to a course
     * Method largely borrowed from Admin with some edits
     * @param course Course to be registered to
     * @param studentUserId The logged in students user id
     */
    private void assignStudentToCourse(Course course, String studentUserId) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        CollectionReference coursesCollection = db.collection("Course");
        logger.info("Assigning student {} to course {}. Current enrolled count: {}, Capacity: {}", studentUserId, course.getCourseCRN(), course.getCurrentEnrolledCount(), course.getCapacity());
        // Query the students collection for a student with the matching UserId
        ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", studentUserId).get();
        try {
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
            if (!studentDocs.isEmpty()) {
                DocumentSnapshot studentSnapshot = studentDocs.getFirst();
                DocumentReference studentRef = studentSnapshot.getReference();
                // Prepare the enrollment details map with timestamp and status
                Map<String, Object> enrollmentDetails = new HashMap<>();
                enrollmentDetails.put("DateEnrolled", System.currentTimeMillis());
                enrollmentDetails.put("EnrollmentStatus", "Active");
                // Add or update the enrolled courses map in the student document
                Object enrolledCoursesObj = studentSnapshot.get("EnrolledCourses");
                Map<String, Object> enrolledCourses;
                if (enrolledCoursesObj instanceof Map) {
                    enrolledCourses = (Map<String, Object>) enrolledCoursesObj;
                } else {
                    enrolledCourses = new HashMap<>();
                }
                // Update the enrolled courses map
                enrolledCourses.put(course.getCourseCRN(), enrollmentDetails);
                coursesTable.refresh();
                // Update the student document with enrolled courses
                studentRef.update("EnrolledCourses", enrolledCourses);
                ApiFuture<QuerySnapshot> courseQuery = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
                List<QueryDocumentSnapshot> courseDocs = courseQuery.get().getDocuments();
                if (!courseDocs.isEmpty()) {
                    DocumentReference courseRef = courseDocs.getFirst().getReference();
                    // Update course capacity and increment the enrolled count
                    course.incrementEnrolledCount();
                    courseRef.update("currentEnrolledCount", course.getCurrentEnrolledCount());
                    // add student ID to enrolled students list by combining
                    courseRef.update("enrolledStudents", FieldValue.arrayUnion(studentUserId));
                    // Add student to course's enrolled list locally for UI purposes
                    course.getEnrolledStudents().add(studentUserId);
                    logger.info("Student {} has been assigned to course {} successfully.", studentUserId, course.getCourseCRN());
                    //showAlert("Student " + studentUserId + " has been assigned to course " + course.getCourseCRN() + ".");
                    showAlert(String.format("Successfully registered to course %s successfully.", course.getCourseCRN()));
                    coursesTable.refresh();
                } else {
                    logger.error("No course found with CRN: {}", course.getCourseCRN());
                    showAlert("No course found with CRN: " + course.getCourseCRN());
                }
            } else {
                logger.warn("Student ID not found: " + studentUserId);
                showAlert("Student ID not found: " + studentUserId);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error assigning student {} to course {}: ", studentUserId, course.getCourseCRN(), e);
        }
    }

    /**
     * Helper method that assigns a student to the waitlist
     * Taken from CoursesController with minimal changes.
     * @param course Course to be waitlisted for
     * @param studentUserId The logged in student's id
     */
    private void assignStudentToWaitlist(Course course, String studentUserId) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference coursesCollection = db.collection("Course");
        CollectionReference studentsCollection = db.collection("Student");
        try {
            //Query Firestore to find the course document with the given CRN
            ApiFuture<QuerySnapshot> courseFuture = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
            List<QueryDocumentSnapshot> courseDocuments = courseFuture.get().getDocuments();
            if (courseDocuments.isEmpty()) {
                Platform.runLater(() -> showAlert("No course found with CRN: " + course.getCourseCRN()));
                logger.error("No course found with CRN: {}", course.getCourseCRN());
                return;
            }
            DocumentReference courseRef = courseDocuments.getFirst().getReference();

            ApiFuture<QuerySnapshot> studentFuture = studentsCollection.whereEqualTo("UserId", studentUserId).get();
            List<QueryDocumentSnapshot> studentDocuments = studentFuture.get().getDocuments();
            if (studentDocuments.isEmpty()) {
                Platform.runLater(() -> showAlert("No student found with UserId: " + studentUserId));
                logger.error("No student found with UserId: {}", studentUserId);
                return;
            }
            DocumentReference studentRef = studentDocuments.getFirst().getReference();

            db.runTransaction(transaction -> {
                //Read data for the course
                DocumentSnapshot courseSnapshot = transaction.get(courseRef).get();
                Object waitlistedStudentsObj = courseSnapshot.get("waitlistedStudents");
                List<Map<String, Object>> waitlistedStudents;
                if (waitlistedStudentsObj instanceof List) {
                    waitlistedStudents = (List<Map<String, Object>>) waitlistedStudentsObj;
                } else {
                    waitlistedStudents = new ArrayList<>();
                }
                //Check if the user is already waitlisted
                boolean alreadyWaitlisted = waitlistedStudents.stream()
                        .anyMatch(student -> studentUserId.equals(student.get("studentUserId")));
                if (alreadyWaitlisted) {
                    throw new IllegalStateException("Student is already waitlisted for the course.");
                }

                //Read data for the student
                DocumentSnapshot studentSnapshot = transaction.get(studentRef).get();
                Object enrolledCoursesObj = studentSnapshot.get("EnrolledCourses");
                List<Map<String, Object>> enrolledCourses = enrolledCoursesObj instanceof List
                        ? (List<Map<String, Object>>) enrolledCoursesObj
                        : new ArrayList<>();

                //Perform writes, add student to course waitlist
                Map<String, Object> waitlistDetails = new HashMap<>();
                waitlistDetails.put("DateWaitlisted", System.currentTimeMillis());
                waitlistDetails.put("Status", "WAITLIST");

                Map<String, Object> waitlistedStudentMap = new HashMap<>();
                waitlistedStudentMap.put("studentUserId", studentUserId);
                waitlistedStudentMap.put("details", waitlistDetails);

                waitlistedStudents.add(waitlistedStudentMap);
                transaction.update(courseRef, "waitlistedStudents", waitlistedStudents);
                transaction.update(courseRef, "currentWaitlistCount", FieldValue.increment(1));

                //Add course to student's enrolled courses list
                Map<String, Object> enrolledCourseDetails = new HashMap<>();
                enrolledCourseDetails.put("courseCRN", course.getCourseCRN());
                enrolledCourseDetails.put("status", "WAITLIST");
                enrolledCourseDetails.put("DateWaitlisted", System.currentTimeMillis());

                enrolledCourses.add(enrolledCourseDetails);
                transaction.update(studentRef, "EnrolledCourses", enrolledCourses);

                return null;
            }).get();

            course.incrementWaitlistCount();
            Platform.runLater(() -> {
                coursesTable.refresh();
                showAlert("Student " + studentUserId + " was successfully added to waitlist for course " + course.getCourseName() + " (CRN: " + course.getCourseCRN() + ")");
            });
            logger.info("Student {} successfully added to waitlist for course {}", studentUserId, course.getCourseCRN());
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Failed to add student " + studentUserId + " to waitlist: " + e.getMessage()));
            logger.error("Failed to add student {} to waitlist for course {}: {}", studentUserId, course.getCourseCRN(), e.getMessage() + e);
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

}
