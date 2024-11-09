package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import course.Course;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import user.Admin;
import user.Professor;
import user.Student;
import user.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for the Accounts.fxml file.
 */
public class AccountsController {

    //TableView where accounts stored in Firebase are displayed
    @FXML
    private TableView<User> accountsTable;

    //Column where a users type is displayed
    @FXML
    private TableColumn<User, String> columnUserType;

    //Column where a users id is displayed
    @FXML
    private TableColumn<User, String> columnId;

    //Column where a users first name is displayed
    @FXML
    private TableColumn<User, String> columnFirstName;

    //Column where a users last name is displayed
    @FXML
    private TableColumn<User, String> columnLastName;

    //Column where a users username is displayed
    @FXML
    private TableColumn<User, String> columnUsername;

    //Column where a users email is displayed
    @FXML
    private TableColumn<User, String> columnEmail;

    //Column where a users department or major is displayed
    @FXML
    private TableColumn<User, String> columnDept;

    //selectedUser is the currently selected item from the TableView
    //Updated when user selects an item in the table view
    private User selectedUser;

    /**
     * Runs when page is loaded. Each column in the TableView is associated with a variable in the User class
     */
    public void initialize() {
        //Setting the column values
        columnUserType.setCellValueFactory(cellData -> {
           User user = cellData.getValue();
           String className = user.getClass().getSimpleName();
           return new SimpleStringProperty(className);
        });
        columnId.setCellValueFactory(new PropertyValueFactory<User, String>("userId"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        columnDept.setCellValueFactory(new PropertyValueFactory<User, String>("userDept"));

        //Now add accounts from Firebase to the TableView
        //Call to helper method where DB is actually read
        handleReadFirebase();
    }

    /**
     * Helper method that when called reads all the current accounts in Firebase and adds them to the tableview
     */
    private void handleReadFirebase() {
        Firestore db = FirestoreClient.getFirestore();
        //Get student documents and read into TableView
        readAndAddStudents(db);
        //Now get professor documents and read into TableView
        readAndAddProfessors(db);
        //Now get admin documents and read into TableView
        readAndAddAdmins(db);
    }

    /**
     * When called, reads the student table in firebase and adds the information into the TableView
     * @param db Firestore db passed in
     */
    private void readAndAddStudents(Firestore db) {
        ApiFuture<QuerySnapshot> studentsFuture = db.collection("Student").get();
        List<QueryDocumentSnapshot> studentDocs;
        //Add students to tableview upon getting the documents
        try {
            studentDocs = studentsFuture.get().getDocuments();

            System.out.println("Reading the Students table, putting into tableview");
            //Loop through the list of student documents and add to accountsTable
            for(QueryDocumentSnapshot doc : studentDocs) {
                Student student = new Student();
                student.setFirstName(doc.getString("FirstName"));
                student.setLastName(doc.getString("LastName"));
                student.setUserName(doc.getString("Username"));
                student.setEmail(doc.getString("Email"));
                student.setUserId(doc.getString("UserId"));
                student.setMajor(doc.getString("Major"));
                student.setClassification(doc.getString("Classification"));
                accountsTable.getItems().add(student);
            }
            System.out.println("Successfully added students to tableview");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When called, reads the professor table in firebase and adds the information into the TableView
     * @param db Firestore db passed in
     */
    private void readAndAddProfessors(Firestore db) {
        ApiFuture<QuerySnapshot> professorFuture = db.collection("Professor").get();
        List<QueryDocumentSnapshot> professorDocs;
        //Add professors to tableview upon getting the documents
        try {
            professorDocs = professorFuture.get().getDocuments();

            System.out.println("Reading the Professors table, putting into tableview");
            //Loop through the list of student documents and add to accountsTable
            for(QueryDocumentSnapshot doc : professorDocs) {
                Professor professor = new Professor();
                professor.setFirstName(doc.getString("FirstName"));
                professor.setLastName(doc.getString("LastName"));
                professor.setUserName(doc.getString("Username"));
                professor.setEmail(doc.getString("Email"));
                professor.setUserId(doc.getString("UserId"));
                professor.setDepartment(doc.getString("Department"));
                accountsTable.getItems().add(professor);
            }
            System.out.println("Successfully added professors to tableview");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When called, reads the admin table in firebase and adds the information into the TableView
     * @param db Firestore db passed in
     */
    private void readAndAddAdmins(Firestore db) {
        ApiFuture<QuerySnapshot> adminFuture = db.collection("Admin").get();
        List<QueryDocumentSnapshot> adminDocs;
        //Add admins to tableview upon getting the documents
        try {
            adminDocs = adminFuture.get().getDocuments();

            System.out.println("Reading the Admins table, putting into tableview");
            //Loop through the list of student documents and add to accountsTable
            for(QueryDocumentSnapshot doc : adminDocs) {
                Admin admin = new Admin();
                admin.setFirstName(doc.getString("FirstName"));
                admin.setLastName(doc.getString("LastName"));
                admin.setUserName(doc.getString("Username"));
                admin.setEmail(doc.getString("Email"));
                admin.setUserId(doc.getString("UserId"));
                accountsTable.getItems().add(admin);
            }
            System.out.println("Successfully added admins to tableview");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the user selecting an item in the TableView
     * Updates the selectedItem variable
     * @param event mouse click event
     */
    public void handleAccountsTableViewMouseClick(MouseEvent event) {
        ObservableList<User> courses = accountsTable.getItems();
        int selectedIndex = accountsTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0 && selectedIndex < courses.size()) {
            //Sets selectedUser to what was clicked
            selectedUser = courses.get(selectedIndex);
            System.out.println(selectedUser.userInfo());
        }
    }

    /**
     * When called goes to create user page
     */
    public void handleCreateUser() {
        RegistrationApp.returnToCreateUser();
    }

    /**
     * When called deletes a user
     */
    public void handleDeleteUser() {
        System.out.println("Delete user called (currently not implemented)");
    }

    /**
     * Simply sets the root back to the base admin area
     * This button is temporary
     */
    public void backButton() {
        setRoot("admin");
    }

}
