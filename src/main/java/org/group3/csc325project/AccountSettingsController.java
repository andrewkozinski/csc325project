package org.group3.csc325project;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import user.Professor;
import user.Student;
import user.User;

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
    public ChoiceBox editDepartmentField;
    @FXML
    public ChoiceBox editClassificationField;
    @FXML
    public ChoiceBox editAccountTypeField;
    private User selectedUser;

    /**
     * When called, allows a selected user to be edited
     */
    String Username = SessionManager.getLoggedInUsername();
    String Role = SessionManager.getLoggedInUserRole();
    boolean twoFactor = SessionManager.getTwoFAEnabled();
    /*
    public void handleEditUser() {
        AccountsController ac = new AccountsController();
        selectedUser =
        if (selectedUser != null) {
            // Populate fields with user data
            editFirstNameField.setText(selectedUser.getFirstName());
            editLastNameField.setText(selectedUser.getLastName());
            editEmailField.setText(selectedUser.getEmail());
            editAgeField.setText(selectedUser.getAge());

            if (selectedUser instanceof Professor) {
                editDepartmentField.setValue(((Professor) selectedUser).getDepartment());
                editClassificationField.setDisable(true); // Disable classification if not applicable...
            } else if (selectedUser instanceof Student) {
                editClassificationField.setValue(((Student) selectedUser).getClassification());
                editDepartmentField.setDisable(true); // Disable department if not applicable...
            } else {
                editClassificationField.setDisable(true);
                editDepartmentField.setDisable(true);
            }
            // Show the edit pane

        } else {
            //showAlert("Edit User", "Please select a user to edit.");
        }
    }
    public void handleSaveUser() {
        if (selectedUser != null) {
            selectedUser.setFirstName(editFirstNameField.getText());
            selectedUser.setLastName(editLastNameField.getText());
            selectedUser.setEmail(editEmailField.getText());
            selectedUser.setAge(editAgeField.getText());

            if (selectedUser instanceof Professor) {
                ((Professor) selectedUser).setDepartment(editDepartmentField.getValue());
            } else if (selectedUser instanceof Student) {
                ((Student) selectedUser).setClassification(editClassificationField.getValue());
            }

            //saveUpdatedUserToDatabase(selectedUser);

        }
    }
    public void handleCancelEdit() {
        // Simply hide the edit pane without saving

    }

     */


}
