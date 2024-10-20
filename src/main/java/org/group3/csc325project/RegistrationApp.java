package org.group3.csc325project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;


import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
public class RegistrationApp extends Application {
    @FXML
    private ImageView login_background_image_view;
    @FXML
    private ImageView login_title_background;
    @FXML
    private ImageView login_input_background;
    @FXML
    private TextField login_userID_title;
    @FXML
    private TextField login_password_title;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RegistrationApp.class.getResource("login.fxml"));
        scene = new Scene(fxmlLoader.load(), 800, 450);
        stage.setTitle("Atlantis University Registration System");
        stage.setScene(scene);
        //Cursor
        //----------------------------------
        StackPane layout = new StackPane();
        Image cursorImage = new Image(getClass().getResourceAsStream("/Images/dolphin - Copy.png"));
        double hotspotX = cursorImage.getWidth() / 2;
        double hotspotY = cursorImage.getHeight() / 2;
        ImageCursor customCursor = new ImageCursor(cursorImage,hotspotX, hotspotY);
        scene.setCursor(customCursor);
        //----------------------------------
        stage.show();
    }
    @FXML
    public void initialize() {
        // Load the background image
        //----------------------------------
        Image image_login_background_image_view = new Image(getClass().getResourceAsStream("/Images/Atlantis_Background.png"));
        login_background_image_view.setImage(image_login_background_image_view);

        Image image_login_title_background = new Image(getClass().getResourceAsStream("/Images/registrationSystem_header_no_user.png"));
        login_title_background.setImage(image_login_title_background);

        Image image_login_input_background = new Image(getClass().getResourceAsStream("/Images/general_menu_background.png"));
        login_input_background.setImage(image_login_input_background);
        //----------------------------------

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
    }