package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import course.Grade;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import user.Student;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.raiseAlert;
import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for studentgrades.fxml, gets the students grades and displays them in a TableView
 */
public class StudentGradesController {
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
    @FXML
    public ImageView account_button;
    @FXML
    public Label account_Name_label;
    @FXML
    public Label account_button_hiitbox;
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

        //Method call that handles reading Firebase and pulling the student's grades from there
        handleReadFirebase();
        String username = SessionManager.getLoggedInUsername();
        account_Name_label.setText(username);
        account_Name_label.setAlignment(Pos.CENTER);
        account_Name_label.setTextAlignment(TextAlignment.CENTER);
        account_Name_label.setFont(Font.font(account_Name_label.getFont().getFamily(), 20));
    }

    /**
     * Helper method that when called reads Firebase to get a students grades for a given course
     */
    private void handleReadFirebase() {

        //First let's get the student
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        //Get the logged in student's id
        String loggedInUserId = SessionManager.getLoggedInUsername();
        //Find a student in the student collection matching the user id
        ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", loggedInUserId).get();

        try {
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();

            if(!studentDocs.isEmpty()) {
                //User ids are unique so it should be the first & only result of the query
                DocumentSnapshot studentSnapshot = studentDocs.getFirst();

                //New Student instance
                Student student = new Student();
                student.setFirstName(studentSnapshot.getString("FirstName"));
                student.setLastName(studentSnapshot.getString("LastName"));
                student.setUserId(studentSnapshot.getString("UserId"));
                student.setMajor(studentSnapshot.getString("Major"));
                student.setUserId(studentSnapshot.getString("UserId"));

                //Get and read the Enrolled courses map, we will need the grade from here
                Map<String, Map<String, Object>> enrolled = (Map<String, Map<String, Object>>) studentSnapshot.get("EnrolledCourses");

                //Now we need to get information about all the courses a student is enrolled in and put that information inside a grade object
                //I just call a helper method to do this
                if(enrolled != null) {
                    List<Grade> enrolledCourseInformation = handleGetCourseInformation(student, enrolled, studentSnapshot);
                    //Now add the list of Grade objects to the table
                    gradesTable.getItems().addAll(enrolledCourseInformation);
                }
                //Maybe add an else statement here in the future to add a field in Firebase for enrolledCourses to a student document if they do not have one already
                //This is definitely an edge case so it may be fine in most scenarios

            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Helper method that will read the CRNs in a Student documents EnrolledCourses field and get information about the courses from Firestore and then store that information in a Course object
     * @return List of Grade objects
     */
    private List<Grade> handleGetCourseInformation(Student student, Map<String, Map<String, Object>> enrolled, DocumentSnapshot studentSnapshot) {
        //Initialize the ArrayList that we will use
        List<Grade> enrolledCourseInformation = new ArrayList<>();

        //Make sure we only display courses a student is actually registered for
        //Waitlisted courses should not be displayed
        Set<String> activeCourses = new HashSet<>();
        for (Map.Entry<String, Map<String, Object>> entry : enrolled.entrySet()) {
            if ("Active".equals(entry.getValue().get("EnrollmentStatus"))) {
                activeCourses.add(entry.getKey());
            }
        }

        //For each CRN in the EnrolledCourses map we need to get information about that course and put that information in a Grade object
        for(String courseCrn : activeCourses) {
            //Grade object that we'll add to the list
            Grade grade = new Grade();

            //Set the current student
            grade.setStudent(student);


            //Now we will need to get course information from Firebase
            Course course = getCourseFromFirebase(courseCrn);

            //Check for null
            if(course == null) {
                raiseAlert("Error getting course information",String.format("Could not get information about course with CRN of \"%s\". Course with default information added to table instead.", courseCrn));
                course = new Course();
                course.setCourseCRN(courseCrn);
                course.setCourseName("ERROR");
            }

            //Set the grade value in studentGrade
            Double gradeVal = (Double) enrolled.get(courseCrn).get("Grade");

            //Set that grade value in the grade object
            if(gradeVal != null) {
                grade.setGrade(gradeVal);
            }
            else {
                double defaultGrade = 100;
                grade.setGrade(defaultGrade);
                //Make sure to modify DB to make sure that grades are consistent
                modifyFirebaseGrade(studentSnapshot.getString("UserId"), defaultGrade, course);
            }

            //Now set the course
            grade.setCourse(course);
            //Add the grade to the list
            enrolledCourseInformation.add(grade);

        }

        return enrolledCourseInformation;
    }

    /**
     * Helper method that will actually get the course from Firestore
     * @param crn The CRN of the Course we wish to get information about
     * @return Course object with all course information stored inside
     */
    private Course getCourseFromFirebase(String crn) {
        Firestore db = FirestoreClient.getFirestore();

        //Course to be returned, set to null initially
        Course course = null;

        ApiFuture<QuerySnapshot> courseQuery = db.collection("Course").whereEqualTo("courseCRN", crn).get();

        try {
            List<QueryDocumentSnapshot> courseDocs = courseQuery.get().getDocuments();

            if(!courseDocs.isEmpty()) {
                DocumentSnapshot courseSnapshot = courseDocs.getFirst();
                course = courseSnapshot.toObject(Course.class);
                course.setCourseCRN(courseSnapshot.getString("courseCRN"));
                course.setCourseName(courseSnapshot.getString("courseName"));
                course.setProfessorReference((DocumentReference) courseSnapshot.get("Professor"));
                course.setCourseDescription(courseSnapshot.getString("courseDescription"));
                course.setCourseTime(courseSnapshot.getString("courseTime"));
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return course;
    }

    /**
     * Method that handles modifying the grade in Firebase
     * Used for if a student does not have a course grade set yet, just sets grade to default (100)
     */
    private void modifyFirebaseGrade(String userId, double newGrade, Course currentCourse) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");

        ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", userId).get();

        try {
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
            if(!studentDocs.isEmpty()) {
                DocumentSnapshot studentSnapshot = studentDocs.getFirst();

                Map<String, Map<String, Object>> update = (Map<String, Map<String, Object>>) studentSnapshot.get("EnrolledCourses");
                update.get(currentCourse.getCourseCRN()).put("Grade", newGrade);

                DocumentReference studentRef = studentSnapshot.getReference();
                studentRef.update("EnrolledCourses", update);
                gradesTable.refresh();
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

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
     * Opens account dropdown menu
     */
    public void openAccount_button(MouseEvent event) {
        RegistrationApp.openAccount_button(event);

    }
}
