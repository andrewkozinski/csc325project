package org.group3.csc325project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * On call, loads a specified .fxml file
     * May be a better way of doing this, I'm open to anyone to rewriting this and setRoot()
     * @param fxml passed in fxml file name
     * @return result of fxmlLoader load
     * @throws IOException If IO error occurs, just throws.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Sets the scenes root, changes the scene to a specified scene
     * Again, if there's a better way of doing this, feel free to update or replace this method and loadFXML
     * @param fxml passed in fxml file name
     * @throws IOException If IO error occurs, just throws.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

}