package org.group3.csc325project;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class RegistrationApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationApp.class);
    private StackPane stackPane;
    private static Scene scene;
    private final int heightScene = 450;
    private final int widthScene = 800;
    @Override
    public void start(Stage stage) throws IOException {
        initializeFirebase();
        FXMLLoader fxmlLoader = new FXMLLoader(RegistrationApp.class.getResource("login.fxml"));
        scene = new Scene(fxmlLoader.load(), widthScene, heightScene);
        stage.setMinHeight(heightScene);
        stage.setMinWidth(widthScene);
        stage.setTitle("Atlantis University Registration System");
        stage.setScene(scene);
        //Cursor
        //----------------------------------
        StackPane layout = new StackPane();
        Image cursorImage = new Image(getClass().getResourceAsStream("/Images/dolphin - Copy.png"));
        double hotspotX = cursorImage.getWidth() / 2;
        double hotspotY = cursorImage.getHeight() / 2;
        ImageCursor customCursor = new ImageCursor(cursorImage, hotspotX, hotspotY);
        scene.setCursor(customCursor);
        //----------------------------------

        stage.show();
    }
    public static Firestore initializeFirebase() {
        try {

            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebaseAPI.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return FirestoreClient.getFirestore();
    }
    public static void main(String[] args) {
        launch();
    }

    /**
     * Helper method for setRoot. On call, loads a specified .fxml file
     * May be a better way of doing this, I'm open to anyone to rewriting this and setRoot()
     * @param fxml passed in fxml file name
     * @return result of fxmlLoader load
     * @throws IOException If IO error occurs, just throws.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RegistrationApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Sets the scenes root, changes the scene to a specified scene
     * Again, if there's a better way of doing this, feel free to update or replace this method and loadFXML
     * @param fxml passed in fxml file name
     */
    public static void setRoot(String fxml) {
        try {
            Parent root = loadFXML(fxml);
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            if (stage.getScene() != scene) {
                stage.setScene(scene);
            }
        } catch (IOException e) {
            logger.error("Error setting root for FXML: " + fxml, e);
            throw new RuntimeException(e);
        }
    }
    /**
     * Method that can be called to return to log in scene.
     * Intended to be used in Student, Professor and Admin controllers
     */
    public static void returnToLogin() {
        setRoot("login");
    }
    public static void returnToCreateUser() {
        setRoot("createuser");
    }

    /**
     * Method that when called, displays an alert to the user
     * @param title Title to be displayed in the alert
     * @param message Messaged to be displayed in the alert
     */
    public static void raiseAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Opens account dropdown menu
     */
    public static void openAccount_button(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem accountInformationButton = new MenuItem("Account Information");
        accountInformationButton.setOnAction(actionEvent -> {
            setRoot("accountsettings");
        });
        MenuItem logoutButton = new MenuItem("Logout");
        logoutButton.setOnAction(actionEvent -> {
            setRoot("login");
        });
        contextMenu.getItems().addAll(accountInformationButton, logoutButton);

        Node source = (Node) event.getSource();

        contextMenu.show(source, event.getScreenX(), event.getScreenY());
    }

}

