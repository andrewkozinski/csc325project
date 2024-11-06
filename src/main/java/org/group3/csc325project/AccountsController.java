package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import user.User;

/**
 * Controller for the Accounts.fxml file.
 */
public class AccountsController {

    //TableView where accounts stored in Firebase are displayed
    @FXML
    private TableView<User> accountsTable;

    //Column where a users first name is displayed
    @FXML
    private TableColumn<User, String> columnFirstName;

    //Column where a users last name is displayed
    @FXML
    private TableColumn<User, String> columnLastName;

    //Column where a users username is displayed
    @FXML
    private TableColumn<User, String> columnUsername;

    //Column where a users email is displayed
    @FXML
    private TableColumn<User, String> columnEmail;

    //Column where a users department or major is displayed
    @FXML
    private TableColumn<User, String> columnDept;

    /**
     * Runs when page is loaded. Each column in the TableView is associated with a variable in the User class
     */
    public void initialize() {
        //Setting the column values
        columnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        columnDept.setCellValueFactory(new PropertyValueFactory<User, String>("userDept"));
    }

}
