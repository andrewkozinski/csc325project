package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import user.Admin;
import user.Professor;
import user.Student;
import user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.group3.csc325project.RegistrationApp.setRoot;
import static user.User.logger;

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
    public ChoiceBox<String> editDepartmentField;
    @FXML
    public ChoiceBox<String> editClassificationField;
    @FXML
    public TextField editPasswordField;
    @FXML
    private CheckBox edit2faToggle;

    private User selectedUser;
    private final String username = SessionManager.getLoggedInUsername();
    private final String role = SessionManager.getLoggedInUserRole();
    private final AccountsController ac = new AccountsController();

    /**
     * Runs when the AccountSettings page is loaded
     */
    public void initialize() {
        selectedUser = getCurrentUserFromFirebase();

        if (selectedUser != null) {
            populateFieldsWithUserData();
        } else {
            ac.showAlert("Error", "Unable to load user data. Please try again.");
        }
    }

    /**
     * Fetches the current user data from Firebase based on the session.
     * @return User object containing the user's data.
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
                    return student;
                } else if (role.equals("Professor")) {
                    Professor professor = document.toObject(Professor.class);
                    professor.setUserId(username);
                    professor.setUsername(username);
                    return professor;
                }
                else if (role.equals("Admin")) {
                    Admin admin = document.toObject(Admin.class);
                    admin.setUserId(username);
                    admin.setUsername(username);
                    return admin;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Populates the fields with the user's data.
     */
    private void populateFieldsWithUserData() {
        // Populate common fields
        editFirstNameField.setText(selectedUser.getFirstName());
        editLastNameField.setText(selectedUser.getLastName());
        editEmailField.setText(selectedUser.getEmail());
        editAgeField.setText(selectedUser.getAge());
        editPasswordField.setText(""); // Default password field to blank

        // Populate 2FA toggle
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(role);

        try {
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Boolean is2faEnabled = document.getBoolean("2faEnabled");
                    edit2faToggle.setSelected(is2faEnabled != null && is2faEnabled);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the updated user information to Firebase.
     */
    @FXML
    public void handleSaveUser() {
        if (selectedUser != null) {
            // Update common fields
            selectedUser.setFirstName(editFirstNameField.getText());
            selectedUser.setLastName(editLastNameField.getText());
            selectedUser.setEmail(editEmailField.getText());
            selectedUser.setAge(editAgeField.getText());

            // Check if the password has been updated
            String newPassword = editPasswordField.getText();
            if (newPassword != null && !newPassword.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                selectedUser.setPassword(hashedPassword);
            }

            // Handle 2FA toggle
            boolean is2faEnabled = edit2faToggle.isSelected();
            Firestore db = FirestoreClient.getFirestore();

            if (is2faEnabled) {
                String secretToken = fetchSecretToken(username, role);
                if (secretToken == null) {
                    // Launch 2FA setup if secretToken is null
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("twofa.fxml")); // Update the path
                        Parent root = loader.load();

                        // Show the 2FA setup UI
                        Stage stage = new Stage();
                        stage.setTitle("Two-Factor Authentication Setup");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();

                        // Retrieve the secretToken from TwoFactorAuthController
                        TwoFactorAuthController controller = loader.getController();
                        secretToken = controller.getSecretToken();

                        // Save the secretToken and enable 2FA in the database
                        if (secretToken != null) {
                            saveSecretToken(username, role, secretToken);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        ac.showAlert("Error", "Unable to load Two-Factor Authentication setup.");
                    }
                }

                // Update 2faEnabled to true
                update2faEnabled(true);
            } else {
                // Disable 2FA in the database
                update2faEnabled(false);
                unsetSecretToken(username, role); // Unset the secret token
            }

            saveUserToFirebase(selectedUser, is2faEnabled);

            backHomeButton();
        } else {
            ac.showAlert("Save User", "No user selected for saving.");
        }
    }
    private void unsetSecretToken(String username, String collectionName) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference collection = db.collection(collectionName);
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    DocumentReference docRef = collection.document(document.getId());
                    docRef.update("secretToken", null).get(); // Unset the token
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error unsetting secret token for user: " + username, e);
        }
    }
    public void saveSecretToken(String username, String collectionName, String secretToken) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference collection = db.collection(collectionName);
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    DocumentReference docRef = collection.document(document.getId());
                    docRef.update("secretToken", secretToken).get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving secret token for user: " + username, e);
        }
    }
    /**
     * Updates the user's 2FA status in Firestore.
     */
    private void update2faEnabled(boolean isEnabled) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            CollectionReference collection = db.collection(role);

            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    DocumentReference docRef = collection.document(document.getId());
                    docRef.update("2faEnabled", isEnabled).get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating 2faEnabled status for user: " + username, e);
        }
    }
    private String fetchSecretToken(String username, String collectionName) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            CollectionReference collection = db.collection(collectionName);
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    return document.getString("secretToken");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveUserToFirebase(User user, boolean is2faEnabled) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(role);

        try {
            ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            QuerySnapshot querySnapshot = future.get();

            for (QueryDocumentSnapshot document : querySnapshot) {
                DocumentReference docRef = document.getReference();

                // Create a map for updates
                Map<String, Object> updates = new HashMap<>();
                updates.put("FirstName", user.getFirstName());
                updates.put("LastName", user.getLastName());
                updates.put("Email", user.getEmail());
                updates.put("Age", user.getAge());
                updates.put("2faEnabled", is2faEnabled);

                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    updates.put("Password", user.getPassword());
                }

                docRef.update(updates).get();
                ac.showAlert("Save User", "User information updated successfully!");
                return;
            }

            ac.showAlert("Save User", "User not found in the database.");
        } catch (InterruptedException | ExecutionException e) {
            ac.showAlert("Error", "An error occurred while saving user information: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the appropriate home screen based on the user's role.
     */
    public void backHomeButton() {
        switch (role) {
            case "Admin" -> setRoot("admin");
            case "Professor" -> setRoot("professor");
            case "Student" -> setRoot("student");
        }
    }
}