package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import static org.group3.csc325project.RegistrationApp.setRoot;

/**
 * Controller for admin.fxml, handles user interactions and updating the UI based on admin actions.
 */
public class AdminController {
    @FXML
    public ImageView admin_background_image_view;
    @FXML
    public ImageView admin_title_background;
    @FXML
    public Label account_Name_label;
    @FXML
    public ImageView account_button;
    @FXML
    public ImageView admin_Assign_button;
    @FXML
    public ImageView admin_Courses_button;
    @FXML
    public ImageView admin_sidebar_background;
    @FXML
    public ImageView admin_adminbackground;
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
    public void returnToCreateUser() {
        RegistrationApp.returnToCreateUser();
    }

    /**
     * Goes to accounts.fxml file where a mock tableview has been set up.
     */
    public void goToAccountsView() {
        setRoot("accounts");
        }

    /**
     * Goes to courses.fxml file where a tableview displaying courses will be displayed
     */
    public void goToCoursesView() { setRoot("courses"); }
    /**
     * Opens account dropdown menu
     */
    public void openAccount_button(MouseEvent event) {
        RegistrationApp.openAccount_button(event);

    }


}
