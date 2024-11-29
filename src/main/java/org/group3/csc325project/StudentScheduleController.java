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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML
    public VBox studentVbox;
    @FXML
    public AnchorPane studentAnchorPane;
    @FXML
    public ImageView studentSideBackground;
    @FXML
    public ImageView studentHeader;
    @FXML
    public ImageView studentCoursesButton;
    @FXML
    public ImageView studentGradesButton;
    @FXML
    public ImageView studentSchedule;
    @FXML
    public HBox topHBox;

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

            if (!studentSnapshot.isEmpty()) {
                DocumentSnapshot studentDoc = studentSnapshot.getDocuments().get(0);

                //Handle both Map and List types for EnrolledCourses, prevent ClassCastException
                Object enrolledCoursesObj = studentDoc.get("EnrolledCourses");
                Map<String, Map<String, Object>> enrolledCourses = new HashMap<>();

                if (enrolledCoursesObj instanceof Map) {
                    enrolledCourses = (Map<String, Map<String, Object>>) enrolledCoursesObj;
                } else if (enrolledCoursesObj instanceof List) {
                    List<Map<String, Object>> enrolledCoursesList = (List<Map<String, Object>>) enrolledCoursesObj;
                    for (Map<String, Object> courseEntry : enrolledCoursesList) {
                        String courseCRN = (String) courseEntry.get("courseCRN");
                        enrolledCourses.put(courseCRN, courseEntry);
                    }
                } else if (enrolledCoursesObj != null) {
                    throw new IllegalStateException("Unexpected type for EnrolledCourses: " + enrolledCoursesObj.getClass());
                }

                if (!enrolledCourses.isEmpty()) {
                    //Filter for courses with an enrollment status of "Active"
                    //Reason for this filtering is that the student may be waitlisted for a course in the enrolledcourses field
                    Set<String> activeCourses = new HashSet<>();
                    for (Map.Entry<String, Map<String, Object>> entry : enrolledCourses.entrySet()) {
                        if ("Active".equals(entry.getValue().get("EnrollmentStatus"))) {
                            activeCourses.add(entry.getKey());
                        }
                    }
                    // Now get each active course and stick it in the TableView
                    handleGetCourses(activeCourses);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error reading Firestore for student schedule", e);
        }
    } // end - handleReadFirebase

    /**
     * Helper method which will get all the courses a student is registered for from Firebase and add them to the TableView
     * @param crnSet set containing the CRNs passed in
     */
    private void handleGetCourses(Set<String> crnSet) {
        if (crnSet == null || crnSet.isEmpty()) {
            logger.warn("No CRNs provided for query. Skipping Firestore request.");
            return; //Return early if the set is null or empty
        }

        Firestore db = FirestoreClient.getFirestore();
        CollectionReference coursesCollection = db.collection("Course");

        //Put the set in an ArrayList
        List<String> crnList = new ArrayList<>(crnSet);

        //Get any courses that have a matching CRN
        ApiFuture<QuerySnapshot> future = coursesCollection.whereIn("courseCRN", crnList).get();


        //Now read through the documents and put the information in a course document
        //Just calls a helper method to do this
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (documents.isEmpty()) {
                logger.info("No matching courses found for the provided CRNs.");
            } else {
                for (QueryDocumentSnapshot document : documents) {
                    documentToCourseInstance(document);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching courses from Firestore.", e);
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
            course.setCourseDescription(document.getString("courseDescription"));
            course.setCourseTextbook(document.getString("courseTextbook"));
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
        CollectionReference coursesCollection = db.collection("Course");
        try {
            boolean wasEnrolled = course.getEnrolledStudents().remove(student.getUserId());
            if (wasEnrolled) {
                course.decrementEnrolledCount();
            } else {
                //If the student was not found, display a message and return early
                showAlert("Student not found in course enrollment!");
                return;
            }

            ApiFuture<QuerySnapshot> courseQuery = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
            List<QueryDocumentSnapshot> courseDocs = courseQuery.get().getDocuments();
            if (!courseDocs.isEmpty()) {
                DocumentReference courseRef = courseDocs.getFirst().getReference();
                courseRef.update("currentEnrolledCount", course.getCurrentEnrolledCount());
                courseRef.update("enrolledStudents", course.getEnrolledStudents());
            }

            //Update the student document to remove the course from enrolled courses
            ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", student.getUserId()).get();
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
            if (!studentDocs.isEmpty()) {
                DocumentReference studentRef = studentDocs.getFirst().getReference();
                Object enrolledCoursesObj = studentDocs.getFirst().get("EnrolledCourses");

                if (enrolledCoursesObj instanceof Map) {
                    //Handle Map data structure
                    Map<String, Map<String, Object>> enrolledCourses = (Map<String, Map<String, Object>>) enrolledCoursesObj;
                    enrolledCourses.remove(course.getCourseCRN());
                    studentRef.update("EnrolledCourses", enrolledCourses);
                } else if (enrolledCoursesObj instanceof ArrayList) {
                    //Handle ArrayList data structure
                    List<Map<String, Object>> enrolledCourses = (ArrayList<Map<String, Object>>) enrolledCoursesObj;
                    enrolledCourses.removeIf(courseEntry -> course.getCourseCRN().equals(courseEntry.get("courseCRN")));
                    studentRef.update("EnrolledCourses", enrolledCourses);
                } else {
                    logger.warn("Unrecognized data structure for EnrolledCourses: {}", enrolledCoursesObj.getClass());
                }
            }
            //Process waitlist if applicable
            if (course.getCurrentWaitlistCount() > 0) {
                List<Map<String, Object>> waitlistedStudents = course.getWaitlistedStudents();
                Map<String, Object> firstWaitlistedStudent = waitlistedStudents.removeFirst();
                course.decrementWaitlistCount();

                String waitlistedStudentId = (String) firstWaitlistedStudent.get("studentUserId");

                //Update the student document of the waitlisted student
                ApiFuture<QuerySnapshot> waitlistedStudentQuery = studentsCollection.whereEqualTo("UserId", waitlistedStudentId).get();
                List<QueryDocumentSnapshot> waitlistedStudentDocs = waitlistedStudentQuery.get().getDocuments();
                if (!waitlistedStudentDocs.isEmpty()) {
                    DocumentReference waitlistedStudentRef = waitlistedStudentDocs.getFirst().getReference();

                    Object waitlistedStudentCoursesObj = waitlistedStudentDocs.getFirst().get("EnrolledCourses");
                    Map<String, Map<String, Object>> waitlistedStudentCourses = new HashMap<>();

                    if (waitlistedStudentCoursesObj instanceof Map) {
                        waitlistedStudentCourses = (Map<String, Map<String, Object>>) waitlistedStudentCoursesObj;
                    } else if (waitlistedStudentCoursesObj instanceof ArrayList) {
                        List<Map<String, Object>> coursesList = (ArrayList<Map<String, Object>>) waitlistedStudentCoursesObj;
                        for (Map<String, Object> courseMap : coursesList) {
                            waitlistedStudentCourses.put((String) courseMap.get("courseCRN"), courseMap);
                        }
                    }
                    //Add the course to the student's enrolled courses
                    Map<String, Object> enrollmentDetails = new HashMap<>();
                    enrollmentDetails.put("DateEnrolled", System.currentTimeMillis());
                    enrollmentDetails.put("EnrollmentStatus", "Active");
                    waitlistedStudentCourses.put(course.getCourseCRN(), enrollmentDetails);
                    waitlistedStudentRef.update("EnrolledCourses", waitlistedStudentCourses);

                    //Add the student to the enrolled list of the course
                    course.incrementEnrolledCount();
                    course.getEnrolledStudents().add(waitlistedStudentId);
                    courseDocs.getFirst().getReference().update("enrolledStudents", course.getEnrolledStudents());
                    courseDocs.getFirst().getReference().update("currentEnrolledCount", course.getCurrentEnrolledCount());
                }
                //Update the course document to reflect the updated waitlist
                courseDocs.getFirst().getReference().update("waitlistedStudents", waitlistedStudents);
                courseDocs.getFirst().getReference().update("currentWaitlistCount", course.getCurrentWaitlistCount());
            }
            coursesTable.refresh();
            showAlert("Student " + student.getUserId() + " has been removed from the course " + course.getCourseCRN() + ".");
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
     * Upon call, switches scene to studentenroll.fxml
     */
    public void goToEnrollPage() {
        setRoot("studentenroll");
    }

    /**
     * Upon call, switches scene to studentschedule.fxml
     */
    public void goToSchedulePage() {
        setRoot("studentschedule");
    }

    /**
     * Upon call, switches scene to studentgrades.fxml
     */
    public void goToGradesPage() {
        setRoot("studentgrades");
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