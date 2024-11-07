package org.group3.csc325project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.Admin;
import user.Professor;
import user.Student;
import user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CreateUserController {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField emailField;
    @FXML
    private ChoiceBox<String> majorField;
    @FXML
    private ChoiceBox<String> classificationField;
    @FXML
    private ChoiceBox<String> accountTypeField;
    @FXML
    private ChoiceBox<String> departmentField;
    @FXML
    private Button createUserButton;

    private static final Map<String, Function<UserParams, User>> userFactory = new HashMap<>();

    static {
        userFactory.put("Student", params -> new Student(params.username, params.hashedPassword, params.firstName, params.lastName, params.userId, params.classification, params.major));
        userFactory.put("Professor", params -> new Professor(params.username, params.hashedPassword, params.firstName, params.lastName, params.userId, params.department));
        userFactory.put("Admin", params -> new Admin(params.username, params.hashedPassword, params.firstName, params.lastName, params.userId));
    }

    @FXML
    public void initialize() {
        accountTypeField.getItems().addAll("Admin", "Student", "Professor");
        classificationField.getItems().addAll("Freshman", "Sophomore", "Junior", "Senior", "Staff");
        majorField.getItems().addAll("Computer Science", "Mathematics", "Physics", "Biology", "Chemistry", "Business", "Economics", "Psychology");
        departmentField.getItems().addAll("Computer Science", "Mathematics", "Physics", "Biology", "Chemistry", "Business", "Economics", "Psychology");
        createUserButton.setOnAction(event -> handleCreateUserButtonClick());
    }

    private void handleCreateUserButtonClick() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String age = ageField.getText().trim();
        String email = emailField.getText().trim();
        String major = majorField.getValue();
        String classification = classificationField.getValue();
        String password = passwordField.getText().trim();
        String accountType = accountTypeField.getValue();
        String department = departmentField.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || email.isEmpty() || password.isEmpty() || accountType == null ||
                (accountType.equals("Student") && (classification == null || major == null)) ||
                (accountType.equals("Professor") && department == null)) {
            showAlert("Please ensure all fields are adequately entered!");
            return;
        }

        String userId = generateUserId(firstName, lastName);
        String username = generateUsername(firstName, lastName);

        if (ifUserIdExists(userId)) {
            showAlert("This user ID already exists. Please try another.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        UserParams params = new UserParams(username, hashedPassword, firstName, lastName, userId, classification, major, department);
        User user = userFactory.getOrDefault(accountType, p -> new Admin(p.username, p.hashedPassword, p.firstName, p.lastName, p.userId)).apply(params);

        if (user == null) {
            showAlert("Failed to create user. Please try again.");
            return;
        }

        saveUserToDatabase(user, age, email, accountType);
    }

    private String generateUserId(String firstName, String lastName) {
        Firestore db = FirestoreClient.getFirestore();
        String baseUserId = (lastName.length() >= 4 ? lastName.substring(0, 4) : lastName) + firstName.charAt(0);
        int suffix = 1;
        String userId;
        do {
            userId = baseUserId.toLowerCase() + String.format("%02d", suffix);
            suffix++;
        } while (ifUserIdExists(userId));
        return userId;
    }

    private String generateUsername(String firstName, String lastName) {
        Firestore db = FirestoreClient.getFirestore();
        String baseUsername = (lastName.length() >= 4 ? lastName.substring(0, 4) : lastName) + firstName.charAt(0);
        int suffix = 1;
        String username;
        do {
            username = baseUsername.toLowerCase() + String.format("%02d", suffix);
            suffix++;
        } while (isUsernameExists(username));
        return username;
    }

    private boolean ifUserIdExists(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        String[] accountTypes = {"Admin", "Student", "Professor"};

        for (String accountType : accountTypes) {
            CollectionReference collection = db.collection(accountType);
            ApiFuture<com.google.cloud.firestore.QuerySnapshot> future = collection.whereEqualTo("UserId", userId).get();
            try {
                if (!future.get().isEmpty()) {
                    return true;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameExists(String username) {
        Firestore db = FirestoreClient.getFirestore();
        String[] accountTypes = {"Admin", "Student", "Professor"};

        for (String accountType : accountTypes) {
            CollectionReference collection = db.collection(accountType);
            ApiFuture<com.google.cloud.firestore.QuerySnapshot> future = collection.whereEqualTo("Username", username).get();
            try {
                if (!future.get().isEmpty()) {
                    return true;
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error: ", e);
                return true;
            }
        }
        return false;
    }

    private void saveUserToDatabase(User user, String age, String email, String accountType) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(accountType);

        Map<String, Object> userData = new HashMap<>();
        userData.put("Username", user.getUsername());
        userData.put("Password", user.getPassword());
        userData.put("FirstName", user.getFirstName());
        userData.put("LastName", user.getLastName());
        userData.put("UserId", user.getUserId());
        userData.put("Age", age);
        userData.put("Email", email);
        if (user instanceof Student) {
            userData.put("Major", ((Student) user).getMajor());
            userData.put("Classification", ((Student) user).getClassification());
        } else if (user instanceof Professor) {
            userData.put("Department", ((Professor) user).getDepartment());
        }
        userData.put("2faEnabled", false);
        userData.put("secretToken", null);

        ApiFuture<DocumentReference> future = collection.add(userData);
        try {
            DocumentReference documentReference = future.get();
            showAlert("User created successfully with Username: " + user.getUsername());
        } catch (InterruptedException | ExecutionException e) {
            showAlert("An error occurred while creating the user. Please try again.");
            logger.error("Error: ", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create User");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class UserParams {
        String username;
        String hashedPassword;
        String firstName;
        String lastName;
        String userId;
        String classification;
        String major;
        String department;

        UserParams(String username, String hashedPassword, String firstName, String lastName, String userId, String classification, String major, String department) {
            this.username = username;
            this.hashedPassword = hashedPassword;
            this.firstName = firstName;
            this.lastName = lastName;
            this.userId = userId;
            this.classification = classification;
            this.major = major;
            this.department = department;
        }
    }
}