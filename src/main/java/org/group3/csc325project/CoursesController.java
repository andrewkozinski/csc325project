package org.group3.csc325project;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Professor;
import user.Student;

import java.util.*;
import java.util.concurrent.ExecutionException;
import static org.group3.csc325project.RegistrationApp.setRoot;
/**
 * Controller for the courses.fxml file
 */
public class CoursesController {
    private static final Logger logger = LoggerFactory.getLogger(CoursesController.class);
    @FXML
    public VBox adminVbox;
    @FXML
    public ImageView adminSideBackground;
    @FXML
    public ImageView adminHeader;
    @FXML
    public ImageView account_button;
    @FXML
    public Label account_Name_label;
    @FXML
    public Label account_button_hitbox;
    //TableView where Courses stored in Firebase are displayed
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
    //Column where the course description is displayed
    @FXML
    private TableColumn<Course, String> columnCourseDescription;
    //Column where the course textbook is displayed
    @FXML
    private TableColumn<Course, String> columnCourseTextbook;
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
    @FXML
    private TableView<Student> waitlistTable;
    @FXML
    private TableColumn<Student, String> columnWaitlistStudentName;
    @FXML
    private TableColumn<Student, String> columnWaitlistStudentID;
    @FXML
    private TableColumn<Student, String> columnWaitlistDate;
    private Course selectedCourse;
    private Student selectedStudent;
    @FXML
    private HBox topHBox;
    @FXML
    private AnchorPane coursesAnchorPane;
    @FXML
    private javafx.scene.image.ImageView adminCourseButton;
    @FXML
    private javafx.scene.image.ImageView adminAccountButton;
    /**
     * Runs when the page is loaded. Each column in the TableView is associated with a variable in the Course class
     */
    public void initialize() {
        // Existing initialization code for courses...
        columnCrn.setCellValueFactory(new PropertyValueFactory<>("courseCRN"));
        columnCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        columnCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        // Modify the instructor column to use the professorNameProperty
        columnInstructor.setCellValueFactory(cellData -> cellData.getValue().professorNameProperty());
        columnCredits.setCellValueFactory(cellData -> {
            String returnString = String.format("%d", cellData.getValue().getCredits());
            return new SimpleStringProperty(returnString);
        });
        columnCourseDescription.setCellValueFactory(new PropertyValueFactory<>("courseDescription"));
        columnCourseTextbook.setCellValueFactory(new PropertyValueFactory<>("courseTextbook"));
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
        // Initialize student columns
        columnStudentName.setCellValueFactory(cellData -> {
            String returnString = String.format("%s %s", cellData.getValue().getFirstName(), cellData.getValue().getLastName());
            return new SimpleStringProperty(returnString);
        });
        columnStudentID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        columnWaitlistStudentName.setCellValueFactory(cellData -> {
            String returnString = String.format("%s %s", cellData.getValue().getFirstName(), cellData.getValue().getLastName());
            return new SimpleStringProperty(returnString);
        });
        columnWaitlistStudentID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        columnWaitlistDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateWaitlistedString()));
        // Set column widths
        columnWaitlistDate.setPrefWidth(200);
        columnWaitlistStudentName.setPrefWidth(150);
        columnStudentName.setPrefWidth(150);
        // UI scalable


        // Reads Course collection in the Firestore database and adds those courses to the TableView
        handleReadFirebase();
        String username = SessionManager.getLoggedInUsername();
        account_Name_label.setText(username);
        account_Name_label.setAlignment(Pos.CENTER);
        account_Name_label.setTextAlignment(TextAlignment.CENTER);
        account_Name_label.setFont(Font.font(account_Name_label.getFont().getFamily(), 20));
    }
    /**
     * Helper method which reads firestore course collection and adds courses to the tableview
     */
    public void handleReadFirebase() {
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
                course.setCourseDescription(document.getString("courseDescription"));
                course.setCourseTextbook(document.getString("courseTextbook"));
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
            logger.error("Error reading Firebase" + e);
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
            //Load students who are enrolled in the selected course
            loadStudentsForSelectedCourse();
            //Load students who are waitlisted for the selected course
            loadWaitlistedStudentsForSelectedCourse();
        }
    }
    /**
     * Loads the students enrolled in the selected course into the studentsTable.
     */
    private void loadStudentsForSelectedCourse() {
        if (selectedCourse == null) {
            return;
        }
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        studentsTable.getItems().clear();
        for (String studentId : selectedCourse.getEnrolledStudents()) {
            ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", studentId).get();
            try {
                List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
                if (!studentDocs.isEmpty()) {
                    DocumentSnapshot studentSnapshot = studentDocs.getFirst();
                    Student student = new Student();
                    student.setFirstName(studentSnapshot.getString("FirstName"));
                    student.setLastName(studentSnapshot.getString("LastName"));
                    student.setUserId(studentSnapshot.getString("UserId"));
                    studentsTable.getItems().add(student);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error loading students for course {}: ", selectedCourse.getCourseCRN(), e);
            }
        }
    }
    private void loadWaitlistedStudentsForSelectedCourse() {
        if (selectedCourse == null) {
            logger.warn("No course selected.");
            return;
        }
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        waitlistTable.getItems().clear();
        List<Map<String, Object>> waitlistedStudents = selectedCourse.getWaitlistedStudents();
        if (waitlistedStudents == null || waitlistedStudents.isEmpty()) {
            logger.info("No waitlisted students found for course {}", selectedCourse.getCourseCRN());
            return;
        }
        logger.info("Loading waitlisted students for course {}: {} students found", selectedCourse.getCourseCRN(), waitlistedStudents.size());
        for (Map<String, Object> waitlistedStudentMap : waitlistedStudents) {
            if (waitlistedStudentMap == null || !waitlistedStudentMap.containsKey("details") || !waitlistedStudentMap.containsKey("studentUserId")) {
                logger.warn("Invalid waitlisted student entry: {}", waitlistedStudentMap);
                continue;
            }
            String studentUserId = (String) waitlistedStudentMap.get("studentUserId");
            if (studentUserId == null) {
                logger.warn("Student UserId is null in waitlisted student map: {}", studentUserId);
                continue;
            }
            Map<String, Object> waitlistedStudent = (Map<String, Object>) waitlistedStudentMap.get("details");
            //Add debug log to check the details map content
            logger.info("Waitlisted student details: {}", waitlistedStudent);
            ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", studentUserId).get();
            try {
                List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
                if (!studentDocs.isEmpty()) {
                    DocumentSnapshot studentSnapshot = studentDocs.getFirst();
                    Student student = new Student();
                    student.setFirstName(studentSnapshot.getString("FirstName"));
                    student.setLastName(studentSnapshot.getString("LastName"));
                    student.setUserId(studentSnapshot.getString("UserId"));
                    //Extracting the correct DateWaitlisted field and formatting it for lovely humans
                    Long dateWaitlisted = (Long) waitlistedStudent.get("DateWaitlisted");
                    if (dateWaitlisted != null) {
                        String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(dateWaitlisted));
                        student.setDateWaitlistedString(formattedDate);
                    }
                    waitlistTable.getItems().add(student);
                    logger.info("Added waitlisted student: {} {} (UserId: {})", student.getFirstName(), student.getLastName(), student.getUserId());
                } else {
                    logger.warn("No student document found for UserId: {}", studentUserId);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error loading waitlisted students for course {}: ", selectedCourse.getCourseCRN(), e);
            }
        }
    }
    /**
     * Method that's called upon the add course button being pressed
     */
    public void handleAddCourseButton() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Enter new course details");
        // Create text fields for each course attribute
        TextField crnField = new TextField();
        TextField codeField = new TextField();
        TextField nameField = new TextField();
        TextField daysField = new TextField();
        TextField timeField = new TextField();
        TextField locationField = new TextField();
        TextField creditsField = new TextField();
        TextField courseDescriptionField = new TextField();
        TextField courseTextbookField = new TextField();
        TextField capacityField = new TextField();
        TextField waitlistField = new TextField();
        ComboBox<Professor> professorComboBox = new ComboBox<>();
        // Populate the ComboBox with professors from Firestore
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference professorsCollection = db.collection("Professor");
        try {
            for (QueryDocumentSnapshot document : professorsCollection.get().get().getDocuments()) {
                String professorId = document.getId();
                String firstName = document.getString("FirstName");
                String lastName = document.getString("LastName");
                if (firstName != null && lastName != null) {
                    professorComboBox.getItems().add(new Professor(professorId, firstName, lastName));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error in handleAddCourseButton method " + e);
        }
        // Set up the grid pane for input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("CRN:"), 0, 0);
        grid.add(crnField, 1, 0);
        grid.add(new Label("Course Code:"), 0, 1);
        grid.add(codeField, 1, 1);
        grid.add(new Label("Course Name:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Days:"), 0, 3);
        grid.add(daysField, 1, 3);
        grid.add(new Label("Time:"), 0, 4);
        grid.add(timeField, 1, 4);
        grid.add(new Label("Location:"), 0, 5);
        grid.add(locationField, 1, 5);
        grid.add(new Label("Credits:"), 0, 6);
        grid.add(creditsField, 1, 6);

        grid.add(new Label("Course Description:"), 0, 7);
        grid.add(courseDescriptionField, 1, 7);
        grid.add(new Label("Course Textbook:"), 0, 8);
        grid.add(courseTextbookField, 1, 8);


        grid.add(new Label("Capacity:"), 0, 9);
        grid.add(capacityField, 1, 9);
        grid.add(new Label("Waitlist:"), 0, 10);
        grid.add(waitlistField, 1, 10);
        grid.add(new Label("Professor:"), 0, 11);
        grid.add(professorComboBox, 1, 11);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // Wait for user response
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Collect input values
                    String crn = crnField.getText().trim();
                    String code = codeField.getText().trim();
                    String name = nameField.getText().trim();
                    String days = daysField.getText().trim();
                    String time = timeField.getText().trim();
                    String location = locationField.getText().trim();
                    int credits = Integer.parseInt(creditsField.getText().trim());
                    String courseDescription = courseDescriptionField.getText().trim();
                    String courseTextbook = courseTextbookField.getText().trim();
                    int capacity = Integer.parseInt(capacityField.getText().trim());
                    int waitlist = Integer.parseInt(waitlistField.getText().trim());
                    Professor selectedProfessor = professorComboBox.getSelectionModel().getSelectedItem();
                    DocumentReference professorReference = null;
                    String professorFullName = "N/A";
                    if (selectedProfessor != null) {
                        professorReference = db.collection("Professor").document(selectedProfessor.getUserId());
                        professorFullName = selectedProfessor.getFirstName() + " " + selectedProfessor.getLastName();
                    }
                    // Create a new course object
                    Course newCourse = new Course(crn, name, code, days, time, location, credits, selectedProfessor, capacity, 0, courseDescription, courseTextbook, waitlist, 0, new ArrayList<>());
                    newCourse.setProfessorReference(professorReference);
                    newCourse.setProfessorName(professorFullName);  // Set the professor name for UI
                    // Save to Firebase - let Firestore generate the document ID automatically
                    addCourseToFirebase(newCourse);
                    // Add to TableView and refresh UI
                    Platform.runLater(() -> {
                        coursesTable.getItems().add(newCourse);
                        coursesTable.refresh();
                    });
                    showAlert("New course added successfully!");
                } catch (NumberFormatException e) {
                    showAlert("Please enter valid numbers for Credits, Capacity, and Waitlist.");
                }
            }
        });
    }
    // Method to save the new course to Firebase
    private void addCourseToFirebase(Course course) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("courseCRN", course.getCourseCRN());
        courseData.put("courseCode", course.getCourseCode());
        courseData.put("courseName", course.getCourseName());
        courseData.put("courseDays", course.getCourseDays());
        courseData.put("courseTime", course.getCourseTime());
        courseData.put("courseLocation", course.getCourseLocation());
        courseData.put("credits", course.getCredits());
        courseData.put("capacity", course.getCapacity());
        courseData.put("currentEnrolledCount", course.getCurrentEnrolledCount());
        courseData.put("courseDescription", course.getCourseDescription());
        courseData.put("courseTextbook", course.getCourseTextbook());
        courseData.put("currentWaitlistCount", course.getCurrentWaitlistCount());
        if (course.getProfessorReference() != null) {
            courseData.put("Professor", course.getProfessorReference());
        }
        try {
            // Let Firestore generate the document ID
            db.collection("Course").add(courseData).get();
            System.out.println("Course successfully added to Firebase!");
        } catch (Exception e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }
    /**
     * Method that's called upon the edit course button being pressed
     */
    public void handleEditCourseButton() {
        if (selectedCourse != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Course");
            dialog.setHeaderText("Editing Course: " + selectedCourse.getCourseCRN());
            // Create text fields for each editable attribute
            TextField crnField = new TextField(selectedCourse.getCourseCRN());
            TextField codeField = new TextField(selectedCourse.getCourseCode());
            TextField nameField = new TextField(selectedCourse.getCourseName());
            TextField daysField = new TextField(selectedCourse.getCourseDays());
            TextField timeField = new TextField(selectedCourse.getCourseTime());
            TextField locationField = new TextField(selectedCourse.getCourseLocation());
            TextField creditsField = new TextField(String.valueOf(selectedCourse.getCredits()));
            TextField descriptionField = new TextField(selectedCourse.getCourseDescription());
            TextField textbookField = new TextField(selectedCourse.getCourseTextbook());
            TextField capacityField = new TextField(String.valueOf(selectedCourse.getCapacity()));
            TextField waitlistField = new TextField(String.valueOf(selectedCourse.getCurrentWaitlistCount()));

            ComboBox<String> professorComboBox = new ComboBox<>();
            professorComboBox.setPrefWidth(200.0);
            // HashMap to map professor names to their document IDs
            Map<String, String> professorNameToIdMap = new HashMap<>();
            Firestore db = FirestoreClient.getFirestore();

            CollectionReference professorsCollection = db.collection("Professor");
            ApiFuture<QuerySnapshot> future = professorsCollection.get();
            try {
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (QueryDocumentSnapshot document : documents) {
                    String professorId = document.getId();
                    String firstName = document.getString("FirstName");
                    String lastName = document.getString("LastName");
                    if (firstName != null && lastName != null) {
                        String fullName = firstName + " " + lastName;
                        // Add the full name to the ComboBox and map it to the ID
                        professorComboBox.getItems().add(fullName);
                        professorNameToIdMap.put(fullName, professorId);
                    } else {
                        System.err.println("Warning: Missing first name or last name for professor with ID: " + professorId);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            // Pre-select the current professor if available
            DocumentReference currentProfessorRef = selectedCourse.getProfessorReference();
            if (currentProfessorRef != null) {
                String currentProfessorId = currentProfessorRef.getId();
                boolean professorFound = false;
                // Find the full name associated with this ID
                for (Map.Entry<String, String> entry : professorNameToIdMap.entrySet()) {
                    if (entry.getValue().equals(currentProfessorId)) {
                        professorComboBox.setValue(entry.getKey());
                        professorFound = true;
                        break;
                    }
                }
                if (!professorFound) {
                    System.err.println("Error: Could not find professor for ID: " + currentProfessorId);
                }
            }
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("CRN:"), 0, 0);
            grid.add(crnField, 1, 0);
            grid.add(new Label("Course Code:"), 0, 1);
            grid.add(codeField, 1, 1);
            grid.add(new Label("Course Name:"), 0, 2);
            grid.add(nameField, 1, 2);
            grid.add(new Label("Days:"), 0, 3);
            grid.add(daysField, 1, 3);
            grid.add(new Label("Time:"), 0, 4);
            grid.add(timeField, 1, 4);
            grid.add(new Label("Location:"), 0, 5);
            grid.add(locationField, 1, 5);
            grid.add(new Label("Credits:"), 0, 6);
            grid.add(creditsField, 1, 6);


            grid.add(new Label("Description:"), 0, 7);
            grid.add(descriptionField, 1, 7);
            grid.add(new Label("Textbook:"), 0, 8);
            grid.add(textbookField, 1, 8);

            grid.add(new Label("Capacity:"), 0, 9);
            grid.add(capacityField, 1, 9);
            grid.add(new Label("Waitlist:"), 0, 10);
            grid.add(waitlistField, 1, 10);
            grid.add(new Label("Professor:"), 0, 11);
            grid.add(professorComboBox, 1, 11);
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Update the selected course with the new data
                    selectedCourse.setCourseCRN(crnField.getText().trim());
                    selectedCourse.setCourseCode(codeField.getText().trim());
                    selectedCourse.setCourseName(nameField.getText().trim());
                    selectedCourse.setCourseDays(daysField.getText().trim());
                    selectedCourse.setCourseTime(timeField.getText().trim());
                    selectedCourse.setCourseLocation(locationField.getText().trim());
                    selectedCourse.setCredits(Integer.parseInt(creditsField.getText().trim()));
                    selectedCourse.setCourseDescription(descriptionField.getText().trim());
                    selectedCourse.setCourseTextbook(textbookField.getText().trim());
                    selectedCourse.setCapacity(Integer.parseInt(capacityField.getText().trim()));
                    selectedCourse.setCurrentWaitlistCount(Integer.parseInt(waitlistField.getText().trim()));

                    String selectedProfessorName = professorComboBox.getValue();
                    if (selectedProfessorName != null && professorNameToIdMap.containsKey(selectedProfessorName)) {
                        String selectedProfessorId = professorNameToIdMap.get(selectedProfessorName);
                        DocumentReference newProfessorRef = db.collection("Professor").document(selectedProfessorId);
                        selectedCourse.setProfessorReference(newProfessorRef);
                        // Set professor name to be displayed in the TableView
                        selectedCourse.setProfessorName(selectedProfessorName);
                    }
                    // Debug print statements
                    logger.debug("Updated professor reference for course: " + selectedCourse.getProfessorReference());
                    logger.debug("Updated professor name for course: {}", selectedCourse.getProfessorName());
                    // Update Firestore with all edited fields
                    updateCourseInFirebase(selectedCourse);
                    // Refresh the data in the TableView
                    coursesTable.refresh(); // Refresh the table to reflect the updated professor
                    showAlert("Your course changes for " + selectedCourse.getCourseName() + " have been saved.");
                }
            });
        }
    }
    public void updateCourseInFirebase(Course course) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference coursesCollection = db.collection("Course");
        // Query the Course collection to find the document by CRN
        ApiFuture<QuerySnapshot> future = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                // Assuming courseCRN is unique and there is only one matching document
                DocumentReference courseRef = documents.getFirst().getReference();
                // Prepare the fields to be updated
                Map<String, Object> updates = new HashMap<>();
                updates.put("courseCode", course.getCourseCode());
                updates.put("courseName", course.getCourseName());
                updates.put("courseDays", course.getCourseDays());
                updates.put("courseTime", course.getCourseTime());
                updates.put("courseLocation", course.getCourseLocation());
                updates.put("credits", course.getCredits());
                updates.put("courseDescription", course.getCourseDescription());
                updates.put("courseTextbook", course.getCourseTextbook());
                updates.put("capacity", course.getCapacity());
                updates.put("currentWaitlistCount", course.getCurrentWaitlistCount());
                if (course.getProfessorReference() != null) {
                    updates.put("Professor", course.getProfessorReference());
                }
                ApiFuture<WriteResult> updateFuture = courseRef.update(updates);
                WriteResult result = updateFuture.get();
                System.out.println("Course successfully updated at: " + result.getUpdateTime());
            } else {
                System.err.println("Error: No course found with CRN: " + course.getCourseCRN());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error updating course: " + e.getMessage());
        }
    }
    /**
     * Method that's called upon the assign course button being pressed
     */
    public void assignCourseButton() {
        if (selectedCourse != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Assign Student");
            dialog.setHeaderText("Assigning Student to Course: " + selectedCourse.getCourseCRN());
            dialog.setContentText("Enter Student User ID:");
            String studentUserId = dialog.showAndWait().orElse(null);
            if (studentUserId != null && !studentUserId.trim().isEmpty()) {
                if (selectedCourse.getCurrentEnrolledCount() < selectedCourse.getCapacity()) {
                    //If there is space, enroll the student directly
                    assignStudentToCourse(selectedCourse, studentUserId.trim());
                } else {
                    //If the course is at capacity, assign to waitlist instead
                    assignStudentToWaitlist(selectedCourse, studentUserId.trim());
                }
            }
        }
    }
    /**
     * Assigns a student to a course in Firestore
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
                waitlistTable.refresh();
                studentsTable.refresh();
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
                    showAlert("Student " + studentUserId + " has been assigned to course " + course.getCourseCRN() + ".");
                    coursesTable.refresh();
                    waitlistTable.refresh();
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
    public void handleStudentsTableViewMouseClick(MouseEvent event) {
        ObservableList<Student> students = studentsTable.getItems();
        int selectedIndex = studentsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < students.size()) {
            //Sets selected student to what was clicked
            selectedStudent = students.get(selectedIndex);
            logger.info("Selected student: {} - {}", selectedStudent.getFirstName(), selectedStudent.getUserId());
        }
    }
    public void handleWaitlistTableViewMouseClick(MouseEvent event) {
        ObservableList<Student> waitlistedStudents = waitlistTable.getItems();
        int selectedIndex = waitlistTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < waitlistedStudents.size()) {
            //Sets selected student to what was clicked
            selectedStudent = waitlistedStudents.get(selectedIndex);
            logger.info("Selected waitlisted student: {} - {}", selectedStudent.getFirstName(), selectedStudent.getUserId());
        } else {
            logger.warn("No waitlisted student was selected.");
        }
    }
    public void handleRemoveStudentButton() {
        if (selectedCourse != null) {
            if (selectedStudent != null) {
                if (studentsTable.getItems().contains(selectedStudent)) {
                    logger.info("Removing student {} from enrolled students.", selectedStudent.getUserId());
                    removeStudentFromCourse(selectedCourse, selectedStudent);
                } else if (waitlistTable.getItems().contains(selectedStudent)) {
                    logger.info("Removing student {} from waitlist.", selectedStudent.getUserId());
                    removeStudentFromWaitlist(selectedCourse, selectedStudent);
                } else {
                    logger.warn("Selected student is not in the enrolled list or waitlist.");
                    showAlert("Selected student is not in the enrolled list or waitlist.");
                }
            } else {
                logger.warn("No student selected.");
                showAlert("Please select a student to remove.");
            }
        } else {
            logger.warn("No course selected.");
            showAlert("Please select a course.");
        }
    }
    private void removeStudentFromCourse(Course course, Student student) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        try {
            boolean wasEnrolled = course.getEnrolledStudents().remove(student.getUserId());
            //Update counts based on whether the student was enrolled
            if (wasEnrolled) {
                course.decrementEnrolledCount();
            } else {
                showAlert("Student not found in course enrollment!");
                return;
            }
            CollectionReference coursesCollection = db.collection("Course");
            ApiFuture<QuerySnapshot> courseQuery = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
            List<QueryDocumentSnapshot> courseDocs = courseQuery.get().getDocuments();
            if (!courseDocs.isEmpty()) {
                DocumentReference courseRef = courseDocs.getFirst().getReference();
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
                Object existingEnrolledCourses = studentDocs.getFirst().get("EnrolledCourses");
                Map<String, Map<String, Object>> enrolledCourses = (Map<String, Map<String, Object>>) existingEnrolledCourses;
                enrolledCourses.remove(course.getCourseCRN());
                studentRef.update("EnrolledCourses", enrolledCourses);
            }

            //Process waitlist
            if (course.getCurrentWaitlistCount() > 0) {
                List<Map<String, Object>> waitlistedStudents = course.getWaitlistedStudents();
                Map<String, Object> firstWaitlistedStudent = waitlistedStudents.getFirst();
                String waitlistedStudentId = (String) firstWaitlistedStudent.get("studentUserId");

                //Update the waitlist
                waitlistedStudents.removeFirst();
                course.decrementWaitlistCount();

                //Update course document with new waitlist
                ApiFuture<QuerySnapshot> courseUpdateQuery = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
                List<QueryDocumentSnapshot> courseUpdateDocs = courseUpdateQuery.get().getDocuments();
                if (!courseUpdateDocs.isEmpty()) {
                    DocumentReference courseUpdateRef = courseUpdateDocs.getFirst().getReference();
                    Map<String, Object> waitlistUpdateData = new HashMap<>();
                    waitlistUpdateData.put("currentWaitlistCount", course.getCurrentWaitlistCount());
                    waitlistUpdateData.put("waitlistedStudents", waitlistedStudents);
                    courseUpdateRef.update(waitlistUpdateData);
                }

                //Move the waitlisted student to enrolled
                ApiFuture<QuerySnapshot> waitlistedStudentQuery = studentsCollection.whereEqualTo("UserId", waitlistedStudentId).get();
                List<QueryDocumentSnapshot> waitlistedStudentDocs = waitlistedStudentQuery.get().getDocuments();
                if (!waitlistedStudentDocs.isEmpty()) {
                    DocumentReference waitlistedStudentRef = waitlistedStudentDocs.getFirst().getReference();
                    Object waitlistedEnrolledCourses = waitlistedStudentDocs.getFirst().get("EnrolledCourses");
                    Map<String, Map<String, Object>> waitlistedStudentCourses = (Map<String, Map<String, Object>>) waitlistedEnrolledCourses;
                    Map<String, Object> enrollmentDetails = new HashMap<>();
                    enrollmentDetails.put("DateEnrolled", System.currentTimeMillis());
                    enrollmentDetails.put("EnrollmentStatus", "Active");
                    waitlistedStudentCourses.put(course.getCourseCRN(), enrollmentDetails);
                    waitlistedStudentRef.update("EnrolledCourses", waitlistedStudentCourses);

                    //Add the student to the course's enrolled list
                    course.incrementEnrolledCount();
                    course.getEnrolledStudents().add(waitlistedStudentId);
                    courseUpdateDocs.getFirst().getReference().update("enrolledStudents", course.getEnrolledStudents());
                    courseUpdateDocs.getFirst().getReference().update("currentEnrolledCount", course.getCurrentEnrolledCount());
                }
            }
            loadStudentsForSelectedCourse();
            loadWaitlistedStudentsForSelectedCourse();
            showAlert("Student " + student.getUserId() + " has been removed from course " + course.getCourseCRN() + ".");
            coursesTable.refresh();
            studentsTable.refresh();
            waitlistTable.refresh();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error removing student {} from course {}: ", student.getUserId(), course.getCourseCRN(), e);
        }
    }
    private void removeStudentFromWaitlist(Course course, Student student) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");
        try {
            boolean wasWaitlisted = false;
            //Handle removing from waitlisted students
            List<Map<String, Object>> updatedWaitlist = new ArrayList<>();
            for (Map<String, Object> waitlistedStudent : course.getWaitlistedStudents()) {
                String studentUserId = (String) waitlistedStudent.get("studentUserId");
                if (studentUserId.equals(student.getUserId())) {
                    wasWaitlisted = true;
                } else {
                    updatedWaitlist.add(waitlistedStudent);
                }
            }
            course.setWaitlistedStudents(updatedWaitlist);
            //Update counts based on whether the student was waitlisted
            if (wasWaitlisted) {
                course.decrementWaitlistCount();
            } else {
                showAlert("Student not found in waitlist.");
                return;
            }
            CollectionReference coursesCollection = db.collection("Course");
            ApiFuture<QuerySnapshot> courseQuery = coursesCollection.whereEqualTo("courseCRN", course.getCourseCRN()).get();
            List<QueryDocumentSnapshot> courseDocs = courseQuery.get().getDocuments();
            if (!courseDocs.isEmpty()) {
                DocumentReference courseRef = courseDocs.getFirst().getReference();
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("currentWaitlistCount", course.getCurrentWaitlistCount());
                updatedData.put("waitlistedStudents", course.getWaitlistedStudents());
                courseRef.update(updatedData);
            }
            //Update the student document to remove the course from enrolled courses
            ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", student.getUserId()).get();
            List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();
            if (!studentDocs.isEmpty()) {
                DocumentReference studentRef = studentDocs.getFirst().getReference();
                //Update Firestore student document to remove the waitlisted course
                Object existingEnrolledCourses = studentDocs.getFirst().get("EnrolledCourses");
                List<Map<String, Object>> enrolledCourses;
                if (existingEnrolledCourses instanceof List) {
                    enrolledCourses = (List<Map<String, Object>>) existingEnrolledCourses;
                } else {
                    enrolledCourses = new ArrayList<>();
                }
                //Remove the course from enrolled courses in the student's document
                enrolledCourses.removeIf(courseEntry -> course.getCourseCRN().equals(courseEntry.get("courseCRN")));
                studentRef.update("EnrolledCourses", enrolledCourses);
            }
            //Refresh the waitlist table after successful removal
            loadWaitlistedStudentsForSelectedCourse();
            showAlert("Student " + student.getUserId() + " has been removed from the waitlist for course " + course.getCourseCRN() + ".");
            coursesTable.refresh();
            waitlistTable.refresh();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error removing student {} from waitlist for course {}: ", student.getUserId(), course.getCourseCRN(), e);
        }
    }
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
                    waitlistedStudents = new ArrayList<>((List<Map<String, Object>>) waitlistedStudentsObj);
                } else {
                    waitlistedStudents = new ArrayList<>();
                }

                // Check if the user is already waitlisted
                boolean alreadyWaitlisted = waitlistedStudents.stream()
                        .anyMatch(student -> studentUserId.equals(student.get("studentUserId")));
                if (alreadyWaitlisted) {
                    throw new IllegalStateException("Student is already waitlisted for the course.");
                }

                //Read data for the student
                DocumentSnapshot studentSnapshot = transaction.get(studentRef).get();
                Object enrolledCoursesObj = studentSnapshot.get("EnrolledCourses");
                Map<String, Object> enrolledCourses;
                if (enrolledCoursesObj instanceof Map) {
                    enrolledCourses = new HashMap<>((Map<String, Object>) enrolledCoursesObj);
                } else {
                    enrolledCourses = new HashMap<>();
                }

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
                enrolledCourseDetails.put("EnrollmentStatus", "WAITLIST");
                enrolledCourseDetails.put("DateWaitlisted", System.currentTimeMillis());

                enrolledCourses.put(course.getCourseCRN(), enrolledCourseDetails);
                transaction.update(studentRef, "EnrolledCourses", enrolledCourses);

                return null;
            }).get();

            course.incrementWaitlistCount();
            Platform.runLater(() -> {
                coursesTable.refresh();
                loadWaitlistedStudentsForSelectedCourse();
                waitlistTable.refresh();
                showAlert("Student " + studentUserId + " was successfully added to waitlist for course " + course.getCourseName() + " (CRN: " + course.getCourseCRN() + ")");
            });
            logger.info("Student {} successfully added to waitlist for course {}", studentUserId, course.getCourseCRN());
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Failed to add student " + studentUserId + " to waitlist: " + e.getMessage()));
            logger.error("Failed to add student {} to waitlist for course {}: {}", studentUserId, course.getCourseCRN(), e.getMessage() + e);
        }
    }

    /**
     * Method which will handle deleting a course
     * Will make appropriate updates in Firebase
     */
    public void handleDeleteCourse() {
        if(selectedCourse == null) {
            showAlert("Please select a course to delete.");
            return;
        }

        //show alert to asking if the user really wants to delete the course
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setHeaderText("Are you sure you want to delete this course?");
        alert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {

            Firestore db = FirestoreClient.getFirestore();
            CollectionReference coursesCollection = db.collection("Course");
            ApiFuture<QuerySnapshot> future = coursesCollection.whereEqualTo("courseCRN", selectedCourse.getCourseCRN()).get();

            try {
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                if (documents.isEmpty()) {
                    showAlert("No course found with CRN: " + selectedCourse.getCourseCRN());
                    return;
                }

                //Before deleting, let's make sure we update the students enrolled
                DocumentSnapshot courseDocument = documents.get(0);
                List<String> enrolled = (List<String>) courseDocument.get("enrolledStudents");

                //Get the studentUserIds out of waitlisted
                List<Map<String, Object>> waitlistedArray = (List<Map<String, Object>>) courseDocument.get("waitlistedStudents");
                List<String> waitlisted = new ArrayList<>();
                if(waitlistedArray != null) {
                    for(Map<String, Object> waitlistedStudent : waitlistedArray) {
                        waitlisted.add((String) waitlistedStudent.get("studentUserId"));
                    }
                }

                //Get the students collection
                CollectionReference studentsCollection = db.collection("Student");

                //Check if enrolled is empty, if it's not we're good
                if(enrolled != null && !enrolled.isEmpty()) {

                    for(String studentId : enrolled) {
                        ApiFuture<QuerySnapshot> studentFuture = studentsCollection.whereEqualTo("UserId", studentId).get();
                        List<QueryDocumentSnapshot> studentDocuments = studentFuture.get().getDocuments();
                        if(studentDocuments.isEmpty()) {
                            logger.warn("No student found with UserId: {}", studentId);
                            continue;
                        }
                        DocumentReference studentRef = studentDocuments.getFirst().getReference();
                        studentRef.update("EnrolledCourses." + selectedCourse.getCourseCRN(), FieldValue.delete());
                    }

                }
                //Now let's remove those on the waitlist
                if(!waitlisted.isEmpty()) {
                    for(String studentId : waitlisted) {
                        ApiFuture<QuerySnapshot> studentFuture = studentsCollection.whereEqualTo("UserId", studentId).get();
                        List<QueryDocumentSnapshot> studentDocuments = studentFuture.get().getDocuments();
                        if(studentDocuments.isEmpty()) {
                            logger.warn("No student found with UserId: {}", studentId);
                            continue;
                        }
                        DocumentReference studentRef = studentDocuments.getFirst().getReference();
                        studentRef.update("EnrolledCourses." + selectedCourse.getCourseCRN(), FieldValue.delete());
                    }
                }

                //Now delete the course itself in firebase
                DocumentReference courseRef = documents.getFirst().getReference();
                courseRef.delete();
                coursesTable.getItems().remove(selectedCourse);
                coursesTable.refresh();
                showAlert("Course " + selectedCourse.getCourseCRN() + " has been deleted.");
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error deleting course: " + e.getMessage());
                showAlert("Error deleting course: " + e.getMessage());
            }
        }



    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modify Course System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Simply sets the root back to the base admin area
     * This button is temporary
     */
    public void backButton() {
        setRoot("admin");
    }
    public void coursesBackButton() {setRoot("courses");}
    public void accountsBackButton() {setRoot("accounts");}
    /**
     * Opens account dropdown menu
     */
    public void openAccount_button(MouseEvent event) {
        RegistrationApp.openAccount_button(event);

    }
}