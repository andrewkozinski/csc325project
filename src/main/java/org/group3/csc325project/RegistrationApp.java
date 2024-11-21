package org.group3.csc325project;

import com.google.auth.Credentials;
import com.google.cloud.Service;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;


import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
public class RegistrationApp extends Application {
    private static Scene scene;
    private int heightScene = 450;
    private int widthScene = 800;
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
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
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
    }