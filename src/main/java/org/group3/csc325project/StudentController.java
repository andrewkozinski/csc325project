package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for student.fxml, handles user interactions and updating the UI based on student actions.
 */
public class StudentController {
    @FXML
    public VBox studentVbox;
    @FXML
    public AnchorPane studentAnchorPane;
    @FXML
    public ImageView studentSideBackground;
    @FXML
    public ImageView studentHeader;
    @FXML
    public ImageView studentCoursesButton;
    @FXML
    public ImageView studentGradesButton;
    @FXML
    public ImageView studentSchedule;
    @FXML
    public HBox topHBox;
    @FXML
    public ImageView account_button;
    @FXML
    public Label account_Name_label;
    @FXML
    public Label account_button_hiitbox;
    public void initialize() {
        String username = SessionManager.getLoggedInUsername();
        account_Name_label.setText(username);
        account_Name_label.setAlignment(Pos.CENTER);
        account_Name_label.setTextAlignment(TextAlignment.CENTER);
        account_Name_label.setFont(Font.font(account_Name_label.getFont().getFamily(), 20));

    }
    /**
     * Upon call, switches scene back to login.fxml.
     */
    public void returnToLogin() {
        RegistrationApp.returnToLogin();
    }

    /**
     * Upon call, switches scene to studentenroll.fxml
     */
    public void goToEnrollPage() {
        setRoot("studentenroll");
    }

    /**
     * Upon call, switches scene to studentschedule.fxml
     */
    public void goToSchedulePage() {
        setRoot("studentschedule");
    }

    /**
     * Upon call, switches scene to studentgrades.fxml
     */
    public void goToGradesPage() {
        setRoot("studentgrades");
    }
    /**
     * Opens account dropdown menu
     */
    public void openAccount_button(MouseEvent event) {
        RegistrationApp.openAccount_button(event);

    }
}
