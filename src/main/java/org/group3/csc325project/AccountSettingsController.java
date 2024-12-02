package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import user.Admin;
import user.Professor;
import user.Student;
import user.User;

import java.util.concurrent.ExecutionException;

/**
 * Controller class for the AccountSettings page. Allows users to edit their account information.
 */
public class AccountSettingsController {
    @FXML
    private TextField editFirstNameField;
    @FXML
    public TextField editLastNameField;
    @FXML
    public TextField editEmailField;
    @FXML
    public TextField editAgeField;
    @FXML
    public ChoiceBox editDepartmentField;
    @FXML
    public ChoiceBox editClassificationField;
    @FXML
    public ChoiceBox editAccountTypeField;
    private User selectedUser;

    /**
     * When called, allows a selected user to be edited
     */
    String username = SessionManager.getLoggedInUsername();
    String role = SessionManager.getLoggedInUserRole();
    boolean twoFactor = SessionManager.getTwoFAEnabled();

    /**
     * Runs when the AccountSettings page is loaded
     */
    public void initialize() {
        // Get the current user from Firebase
        selectedUser = getCurrentUserFromFirebase();
    }

    /**
     * Helper method that gets user from firebase and stores information into a User instance
     * @return A new User object instance
     */
    private User getCurrentUserFromFirebase() {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(role);

        try {
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();
            for (QueryDocumentSnapshot document : querySnapshot) {
                if (role.equals("Student")) {
                    Student student = document.toObject(Student.class);
                    student.setUserId(username);
                    student.setUsername(username);
                    student.setMajor(document.getString("Major"));
                    student.setClassification(document.getString("Classification"));
                    student.setAge(document.getString("Age"));
                    student.setFirstName(document.getString("FirstName"));
                    student.setLastName(document.getString("LastName"));
                    student.setEmail(document.getString("Email"));
                    return student;
                } else if (role.equals("Professor")) {
                    Professor professor = document.toObject(Professor.class);
                    professor.setUserId(username);
                    professor.setUsername(username);
                    professor.setDepartment(document.getString("Department"));
                    professor.setAge(document.getString("Age"));
                    professor.setFirstName(document.getString("FirstName"));
                    professor.setLastName(document.getString("LastName"));
                    professor.setEmail(document.getString("Email"));
                    return professor;
                }
                else if(role.equals("Admin")) {
                    Admin admin = document.toObject(Admin.class);
                    admin.setUserId(username);
                    admin.setUsername(username);
                    admin.setAge(document.getString("Age"));
                    admin.setFirstName(document.getString("FirstName"));
                    admin.setLastName(document.getString("LastName"));
                    admin.setEmail(document.getString("Email"));
                    return admin;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return null;

    }

    /*
    public void handleEditUser() {
        AccountsController ac = new AccountsController();
        selectedUser =
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

        } else {
            //showAlert("Edit User", "Please select a user to edit.");
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

            //saveUpdatedUserToDatabase(selectedUser);

        }
    }
    public void handleCancelEdit() {
        // Simply hide the edit pane without saving

    }

     */


}
