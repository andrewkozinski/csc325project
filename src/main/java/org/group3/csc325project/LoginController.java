package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;



public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
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
    private VBox login_screen;
    @FXML
    private AnchorPane login_screen_anchor;

    public void initialize() {


        // Load the background images
        login_background_image_view.setImage(new Image(getClass().getResourceAsStream("/Images/Atlantis_Background.png")));
        login_title_background.setImage(new Image(getClass().getResourceAsStream("/Images/registrationSystem_header_no_user.png")));
        login_input_background.setImage(new Image(getClass().getResourceAsStream("/Images/general_menu_background.png")));
        // Scaling ImagePanes
        login_background_image_view.fitWidthProperty().bind(login_screen.widthProperty());
        login_background_image_view.fitHeightProperty().bind(login_screen.heightProperty());

        login_title_background.fitWidthProperty().bind(login_screen.widthProperty());
        login_title_background.fitHeightProperty().bind(login_screen.heightProperty());

        login_screen_anchor.prefWidthProperty().bind(login_screen.widthProperty());
        login_screen_anchor.prefHeightProperty().bind(login_screen.heightProperty());

        login_input_background.fitWidthProperty().bind(login_screen_anchor.widthProperty());
        login_input_background.fitHeightProperty().bind(login_screen_anchor.heightProperty());
        // Scaling other elements


        login_userID_title.minWidthProperty().bind(login_screen_anchor.widthProperty().multiply(0.3));
        login_password_title.minWidthProperty().bind(login_screen_anchor.widthProperty().multiply(0.3));
        login_userID_input_textField.prefWidthProperty().bind(login_screen_anchor.widthProperty().multiply(0.5));
        login_password_input.prefWidthProperty().bind(login_screen_anchor.widthProperty().multiply(0.5));
        login_login_button.prefWidthProperty().bind(login_screen_anchor.widthProperty().multiply(0.20));
        login_forgot_password_button.prefWidthProperty().bind(login_screen_anchor.widthProperty().multiply(0.50));

        VBox.setMargin(login_forgot_password_button, new Insets(0, 0, 0, 10));
        login_screen.setFillWidth(true);
        login_background_image_view.setPreserveRatio(true);
        login_title_background.setPreserveRatio(true);
        login_input_background.setPreserveRatio(true);



    }

    @FXML
    private void handleLoginButtonClick() {
        String username = login_userID_input_textField.getText().trim();
        String password = login_password_input.getText().trim();

        try {
            if (authenticateUser("Admin", username, password)) return;
            if (authenticateUser("Student", username, password)) return;
            if (authenticateUser("Professor", username, password)) return;

            displayError("Invalid Username or password! Please try again.");

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error: ", e);
            displayError("An error has occurred while logging in! Please try again later.");
        }
    }
    private boolean authenticateUser(String collectionName, String username, String password) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(collectionName);

        ApiFuture<QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
        QuerySnapshot querySnapshot = future.get();

        if (!querySnapshot.isEmpty()) {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                String storedPasswordHash = document.getString("Password");
                Boolean twoFAEnabled = document.getBoolean("2faEnabled");

                if (storedPasswordHash != null && BCrypt.checkpw(password, storedPasswordHash)) { // ADD BETTER ERROR HANDLING OMG..
                    System.out.println("Password matches! =D");

                    SessionManager.setLoggedInUsername(username);
                    SessionManager.setLoggedInUserRole(collectionName);
                    SessionManager.setTwoFAEnabled(Boolean.TRUE.equals(twoFAEnabled));

                    if (Boolean.TRUE.equals(twoFAEnabled)) {
                        System.out.println("2FA is enabled for this user. Redirecting to 2FA screen...");
                        RegistrationApp.setRoot("twofa");
                    } else {
                        System.out.println("2FA is not enabled. Redirecting to user home screen.");
                        redirectToUserHomeScreen(collectionName);
                    }
                    return true;
                } else {
                    System.out.println("Password did not match. Please try again.");
                }
            }
        }
        return false;
    }
    private static final Map<String, Consumer<Void>> roleRedirect = new HashMap<>();
    static {
        roleRedirect.put("Admin", v -> redirectToScreen("admin"));
        roleRedirect.put("Student", v -> redirectToScreen("student"));
        roleRedirect.put("Professor", v -> redirectToScreen("professor"));
    }
    private void redirectToUserHomeScreen(String role) {
        try {
            Consumer<Void> action = roleRedirect.get(role);
            if (action != null) {
                action.accept(null);
            } else {
                displayError("Invalid user role detected.");
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
            displayError("An error occurred while redirecting. Please try again later.");
        }
    }
    private static void redirectToScreen(String fxmlFileName) {
        RegistrationApp.setRoot(fxmlFileName);
    }
    private void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleForgotPasswordButtonClick() {
        showAlert("Forgot Password", "Please contact an administrator to change your password.");
    }
    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}