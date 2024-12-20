package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import course.Grade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import user.Student;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.raiseAlert;
import static org.group3.csc325project.RegistrationApp.setRoot;

public class ProfessorCourseGrades {
    @FXML
    public VBox professorVbox;
    @FXML
    public AnchorPane professorAnchorPane;
    @FXML
    public ImageView professorSideBackground;
    @FXML
    public ImageView professorHeader;
    @FXML
    public ImageView professorCourseButton;
    @FXML
    public HBox topHBox;
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
    @FXML
    public ImageView account_button;
    @FXML
    public Label account_Name_label;
    @FXML
    public Label account_button_hitbox;
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
        //courseLabel.setText(String.format("Student grades for: %s (%s)", currentCourse.getCourseName(), currentCourse.getCourseCode()));

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

        //Now read firebase for grades
        handleReadFirebase();
        String username = SessionManager.getLoggedInUsername();
        account_Name_label.setText(username);
        account_Name_label.setAlignment(Pos.CENTER);
        account_Name_label.setTextAlignment(TextAlignment.CENTER);
        account_Name_label.setFont(Font.font(account_Name_label.getFont().getFamily(), 20));
    }

    /**
     * Helper method that will read Firebase for all grades stored
     */
    private void handleReadFirebase() {

        Firestore db = FirestoreClient.getFirestore();
        CollectionReference studentsCollection = db.collection("Student");

        //For each student enrolled, read their grades
        for(String studentId: currentCourse.getEnrolledStudents()) {

            ApiFuture<QuerySnapshot> studentQuery = studentsCollection.whereEqualTo("UserId", studentId).get();
            try {
                List<QueryDocumentSnapshot> studentDocs = studentQuery.get().getDocuments();

                if (!studentDocs.isEmpty()) {
                    DocumentSnapshot studentSnapshot = studentDocs.getFirst();

                    //Grade object that we will put information inside of
                    Grade studentGrade = new Grade();

                    //New Student instance
                    Student student = new Student();
                    student.setFirstName(studentSnapshot.getString("FirstName"));
                    student.setLastName(studentSnapshot.getString("LastName"));
                    student.setUserId(studentSnapshot.getString("UserId"));
                    student.setMajor(studentSnapshot.getString("Major"));
                    student.setUserId(studentSnapshot.getString("UserId"));

                    //Get and read the Enrolled courses map, we will need the grade from here
                    Map<String, Map<String, Object>> enrolled = (Map<String, Map<String, Object>>) studentSnapshot.get("EnrolledCourses");

                    //Set the grade value in studentGrade
                    Double gradeVal = (Double) enrolled.get(currentCourse.getCourseCRN()).get("Grade");
                    if(gradeVal != null) {
                        studentGrade.setGrade(gradeVal);
                    }
                    else {
                        double defaultGrade = 100;
                        studentGrade.setGrade(defaultGrade);
                        //Make sure to modify DB to make sure that grades are consistent
                        modifyFirebaseGrade(studentSnapshot.getString("UserId"), defaultGrade);
                    }

                    //Add remaining member values and add to table
                    studentGrade.setStudent(student);
                    studentGrade.setCourse(currentCourse);
                    gradesTable.getItems().add(studentGrade);

                }

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }//end for

    } //end - readFirebase

    /**
     * Button to handle modifying a Grade
     */
    public void handleModifyGradeButton() {
        if(selectedGrade != null) {
            System.out.printf("Modifying student %s's grade\n", selectedGrade.getStudent().getUserId());
            //Should text input dialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Modify Grade");
            dialog.setHeaderText(String.format("Modifying %s %s's grade", selectedGrade.getStudent().getFirstName(), selectedGrade.getStudent().getLastName()));
            dialog.setContentText("Enter grade to modify");
            String inputGrade = dialog.showAndWait().orElse(null);
            if(inputGrade != null && !inputGrade.trim().isEmpty()) {
                double grade = Double.parseDouble(inputGrade);
                //Update object grade
                selectedGrade.setGrade(grade);
                //Update grade in Firebase
                modifyFirebaseGrade(selectedGrade.getStudent().getUserId(), grade);
            }
        }
        else {
            //selectedGrade is null so raise alert
            raiseAlert("No Grade Selected", "Please select a grade to modify");
        }
    }

    /**
     * Method that handles modifying the grade in Firebase
     */
    private void modifyFirebaseGrade(String userId, double newGrade) {
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
     * Handles the user selecting an item in the grades TableView
     * Updates the selectedItem variable
     * @param event mouse click event
     */
    public void handleGradesTableMouseClick(MouseEvent event) {
        ObservableList<Grade> gradeItems = gradesTable.getItems();
        int selectedIndex = gradesTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0 && selectedIndex < gradeItems.size()) {
            selectedGrade = gradeItems.get(selectedIndex);
            System.out.printf("Selected grade: %s for Student: %s %s\n", selectedGrade.getGrade(), selectedGrade.getStudent().getFirstName(), selectedGrade.getStudent().getLastName());
        }
    }

    /**
     * When called switches scene back to professor courses page
     */
    public void handleGoBackToCourses() {
        setRoot("professorcourses");
    }
    public void openAccount_button(MouseEvent event) {
        RegistrationApp.openAccount_button(event);

    }

}
