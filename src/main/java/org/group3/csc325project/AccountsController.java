package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Admin;
import user.Professor;
import user.Student;
import user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for the Accounts.fxml file.
 */
public class AccountsController {
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
    @FXML
    public AnchorPane coursesAnchorPane;
    @FXML
    public ImageView account_button;
    @FXML
    public Label account_Name_label;
    @FXML
    public Label account_button_hitbox;

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

    //Column where a users age is displayed
    @FXML
    private TableColumn<User, String> columnAge;

    //selectedUser is the currently selected item from the TableView
    //Updated when user selects an item in the table view
    private User selectedUser;
    @FXML
    private AnchorPane editUserPane;
    @FXML
    private TextField editFirstNameField, editLastNameField, editEmailField, editAgeField;

    @FXML
    private ChoiceBox<String> editDepartmentField, editClassificationField, editAccountTypeField;
    @FXML
    private HBox accountsHBox;
    @FXML
    private javafx.scene.image.ImageView adminAccountButton;
    @FXML
    private javafx.scene.image.ImageView adminCourseButton;
    @FXML
    private HBox adminTableBottonButtons;


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
        columnId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        columnDept.setCellValueFactory(new PropertyValueFactory<>("userDept"));

        //Now add accounts from Firebase to the TableView
        //Call to helper method where DB is actually read
        handleReadFirebase();
        String username = SessionManager.getLoggedInUsername();
        account_Name_label.setText(username);
        account_Name_label.setAlignment(Pos.CENTER);
        account_Name_label.setTextAlignment(TextAlignment.CENTER);
        account_Name_label.setFont(Font.font(account_Name_label.getFont().getFamily(), 20));
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
                student.setUsername(doc.getString("Username"));
                student.setEmail(doc.getString("Email"));
                student.setUserId(doc.getString("UserId"));
                student.setMajor(doc.getString("Major"));
                student.setAge(doc.getString("Age"));
                student.setClassification(doc.getString("Classification"));
                accountsTable.getItems().add(student);
            }
            System.out.println("Successfully added students to tableview");

        } catch (InterruptedException | ExecutionException e) {
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
                professor.setUsername(doc.getString("Username"));
                professor.setEmail(doc.getString("Email"));
                professor.setUserId(doc.getString("UserId"));
                professor.setAge(doc.getString("Age"));
                professor.setDepartment(doc.getString("Department"));
                accountsTable.getItems().add(professor);
            }
            System.out.println("Successfully added professors to tableview");

        } catch (InterruptedException | ExecutionException e) {
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
                admin.setUsername(doc.getString("Username"));
                admin.setEmail(doc.getString("Email"));
                admin.setUserId(doc.getString("UserId"));
                admin.setAge(doc.getString("Age"));
                accountsTable.getItems().add(admin);
            }
            System.out.println("Successfully added admins to tableview");

        } catch (InterruptedException | ExecutionException e) {
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
     * When called, allows a selected user to be edited
     */
    public void handleEditUser() {
        // Get the selected user from the TableView
        selectedUser = accountsTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Populate fields with user data
            editFirstNameField.setText(selectedUser.getFirstName());
            editLastNameField.setText(selectedUser.getLastName());
            editEmailField.setText(selectedUser.getEmail());
            editAgeField.setText(selectedUser.getAge());

            if (selectedUser instanceof Professor) {
                editDepartmentField.setValue(((Professor) selectedUser).getDepartment());
                editClassificationField.setDisable(true); // Disable classification if not applicable...
            } else if (selectedUser instanceof Student) {
                editClassificationField.setValue(((Student) selectedUser).getClassification());
                editDepartmentField.setDisable(true); // Disable department if not applicable...
            } else {
                editClassificationField.setDisable(true);
                editDepartmentField.setDisable(true);
            }
            // Show the edit pane
            editUserPane.setVisible(true);
            accountsTable.setVisible(false);
            adminTableBottonButtons.setVisible(false);
        } else {
            showAlert("Edit User", "Please select a user to edit.");
        }
    }
    public void handleSaveUser() {
        if (selectedUser != null) {
            selectedUser.setFirstName(editFirstNameField.getText());
            selectedUser.setLastName(editLastNameField.getText());
            selectedUser.setEmail(editEmailField.getText());
            selectedUser.setAge(editAgeField.getText());

            if (selectedUser instanceof Professor) {
                ((Professor) selectedUser).setDepartment(editDepartmentField.getValue());
            } else if (selectedUser instanceof Student) {
                ((Student) selectedUser).setClassification(editClassificationField.getValue());
            }

            saveUpdatedUserToDatabase(selectedUser);
            editUserPane.setVisible(false);
            accountsTable.setVisible(true);
            adminTableBottonButtons.setVisible(true);
        }
    }
    public void handleCancelEdit() {
        // Simply hide the edit pane without saving
        editUserPane.setVisible(false);
        accountsTable.setVisible(true);
        adminTableBottonButtons.setVisible(true);
    }
    public void saveUpdatedUserToDatabase(User user) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(user.getClass().getSimpleName());

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("FirstName", user.getFirstName());
        updatedData.put("LastName", user.getLastName());
        updatedData.put("Email", user.getEmail());
        updatedData.put("Age", user.getAge());

        if (user instanceof Professor) {
            updatedData.put("Department", ((Professor) user).getDepartment());
        } else if (user instanceof Student) {
            updatedData.put("Major", ((Student) user).getMajor());
            updatedData.put("Classification", ((Student) user).getClassification());  // Make sure Classification is updated correctly
        }

        ApiFuture<QuerySnapshot> future = collection.whereEqualTo("UserId", user.getUserId()).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentReference docRef = documents.get(0).getReference();
                docRef.update(updatedData);
                showAlert("Edit User", "User updated successfully.");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error updating user: ", e);
            showAlert("Edit User", "Please try again. Failed to edit user.");
        }
    }
    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * When called deletes a user
     */
    public void handleDeleteUser() {
        if (selectedUser == null) {
            showAlert("No user selected", "Please select a user to delete.");
            return;
        }

        Firestore db = FirestoreClient.getFirestore();
        String userCollection = selectedUser.getClass().getSimpleName(); // e.g., "Student", "Professor", "Admin"

        ApiFuture<QuerySnapshot> future = db.collection(userCollection).whereEqualTo("UserId", selectedUser.getUserId()).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                // Delete the user from Firestore
                String documentId = documents.getFirst().getId();
                ApiFuture<WriteResult> writeResult = db.collection(userCollection).document(documentId).delete();
                writeResult.get(); // Wait for delete to complete

                // Remove the user from the TableView
                accountsTable.getItems().remove(selectedUser);
                selectedUser = null; // Clear selected user

                showAlert("Success", "User deleted successfully.");
            }
        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "An error occurred while deleting the user.");
            logger.error("Error deleting user: ", e);
        }
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