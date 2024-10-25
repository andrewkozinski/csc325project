package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.mindrot.jbcrypt.BCrypt;
import java.util.concurrent.ExecutionException;

public class LoginController {
    @FXML
    private ImageView login_background_image_view;
    @FXML
    private ImageView login_title_background;
    @FXML
    private ImageView login_input_background;
    @FXML
    private Label login_userID_title;
    @FXML
    private Label login_password_title;
    @FXML
    private TextField login_2faID_input_textField;
    @FXML
    private TextField login_userID_input_textField;
    @FXML
    private PasswordField login_password_input;
    @FXML
    private Button login_login_button;
    @FXML
    private Button login_forgot_password_button;

    @FXML
    public void initialize() {
        // Load the background images
        //login_background_image_view.setImage(new Image(getClass().getResourceAsStream("/Images/Atlantis_Background.png")));
        login_title_background.setImage(new Image(getClass().getResourceAsStream("/Images/registrationSystem_header_no_user.png")));
        login_input_background.setImage(new Image(getClass().getResourceAsStream("/Images/general_menu_background.png")));
        login_login_button.setOnAction(event -> handleLoginButtonClick());
    }

    @FXML
    private void handleLoginButtonClick() {
        String username = login_userID_input_textField.getText().trim();
        String password = login_password_input.getText().trim();

        try {
            if (checkUserCredentials("Admin", username, password)) {
                System.out.println("Admin login successful!");
                RegistrationApp.setRoot("admin");
                return;
            }
            if (checkUserCredentials("Student", username, password)) {
                System.out.println("Student login successful!");
                RegistrationApp.setRoot("student");
                return;
            }
            if (checkUserCredentials("Professor", username, password)) {
                System.out.println("Professor login successful!");
                RegistrationApp.setRoot("professor");
                return;
            }
            displayError("Invalid username or password! Please try again.");

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            displayError("An error has occurred while logging in! Please try again later.");
        }
    }
    private void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean checkUserCredentials(String collectionName, String username, String password) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(collectionName);

        ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
        QuerySnapshot querySnapshot = future.get();

        if (!querySnapshot.isEmpty()) {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                String storedPasswordHash = document.getString("Password");
                String dbUsername = document.getString("Username");
                Boolean twoFAEnabled = document.getBoolean("2faEnabled");

                if (storedPasswordHash != null && BCrypt.checkpw(password, storedPasswordHash)) {
                    System.out.println("Password matches!");
                    if (Boolean.TRUE.equals(twoFAEnabled)) {
                        System.out.println("2FA is enabled for this user. Redirecting to 2FA screen...");
                        RegistrationApp.setRoot("twofa");
                        return true;
                    }
                    System.out.println("2FA is not enabled, skipping!");
                    return true;
                } else {
                    System.out.println("Password did not match. Please re-try.");
                }
            }
        }
        return false;  // User not found or incorrect password
    }
    @FXML
    private void handleForgotPasswordButtonClick() {
        System.out.println("Forgot Password button clicked.");
        // work in progress - yash
    }
}