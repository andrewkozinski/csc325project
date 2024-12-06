package user;

import com.google.cloud.firestore.annotation.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class representing a user in the registration system.
 * Users can include but are not limited to: Student, Professor, and Admin.
 * Stores information regarding the user such as Username, ID, etc.
 * Various methods with default behavior of a user are defined below.
 * @author Andrew Kozinski
 */
public abstract class User {
    public static final Logger logger = LoggerFactory.getLogger(User.class);

    // User variables
    @PropertyName("Username")
    protected String Username; // User's username

    @PropertyName("Password")
    protected String Password; // User's password (hashed)

    @PropertyName("FirstName")
    protected String FirstName; // User's first name

    @PropertyName("LastName")
    protected String LastName; // User's last name

    @PropertyName("UserId")
    protected String UserId; // Unique user ID

    @PropertyName("Email")
    protected String Email; // User's email address

    @PropertyName("Age")
    protected String Age; // User's age

    @PropertyName("DateWaitlisted")
    protected Long dateWaitlisted; // Date the user was waitlisted

    // Default constructor
    /**
     * Default constructor, sets variable values to default (null).
     */
    public User() {
        Username = null;
        Password = null;
        FirstName = null;
        LastName = null;
        UserId = null;
        Email = null;
        Age = null;
        dateWaitlisted = null;
    }

    // Parameterized Constructor
    /**
     * Parameterized constructor, initializes variables with provided values.
     * @param username Passed in Username.
     * @param password Passed in password.
     * @param firstName Passed in first name.
     * @param lastName Passed in last name.
     * @param userId Passed in user ID.
     * @param age Passed in age.
     */
    public User(String username, String password, String firstName, String lastName, String userId, String age) {
        this.Username = username;
        this.Password = password;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.UserId = userId;
        this.Age = age;
        this.dateWaitlisted = null;
        logger.info("User created with Age: {}", age);
    }

    // Default implementation of User methods below

    /**
     * Returns the Username of the user.
     * @return Username.
     */
    @PropertyName("Username")
    public String getUsername() {
        return Username;
    }

    /**
     * Sets the Username of the user.
     * @param username Username to set.
     */
    @PropertyName("Username")
    public void setUsername(String username) {
        this.Username = username;
    }

    /**
     * Returns the Password of the user.
     * @return Password (hashed).
     */
    @PropertyName("Password")
    public String getPassword() {
        return Password;
    }

    /**
     * Sets the Password of the user.
     * @param password Password to set (should be hashed).
     */
    @PropertyName("Password")
    public void setPassword(String password) {
        this.Password = password;
    }

    /**
     * Returns the User ID of the user.
     * @return User ID.
     */
    @PropertyName("UserId")
    public String getUserId() {
        return UserId;
    }

    /**
     * Sets the User ID of the user.
     * @param userId User ID to set.
     */
    @PropertyName("UserId")
    public void setUserId(String userId) {
        this.UserId = userId;
    }

    /**
     * Returns the first name of the user.
     * @return First name.
     */
    @PropertyName("FirstName")
    public String getFirstName() {
        return FirstName;
    }

    /**
     * Sets the first name of the user.
     * @param firstName First name to set.
     */
    @PropertyName("FirstName")
    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    /**
     * Returns the last name of the user.
     * @return Last name.
     */
    @PropertyName("LastName")
    public String getLastName() {
        return LastName;
    }

    /**
     * Sets the last name of the user.
     * @param lastName Last name to set.
     */
    @PropertyName("LastName")
    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    /**
     * Returns the Email of the user.
     * @return Email address.
     */
    @PropertyName("Email")
    public String getEmail() {
        return Email;
    }

    /**
     * Sets the Email of the user.
     * @param email Email address to set.
     */
    @PropertyName("Email")
    public void setEmail(String email) {
        this.Email = email;
    }

    /**
     * Returns the Age of the user.
     * @return User's age.
     */
    @PropertyName("Age")
    public String getAge() {
        return Age;
    }

    /**
     * Sets the Age of the user.
     * @param age Age to set.
     */
    @PropertyName("Age")
    public void setAge(String age) {
        this.Age = age;
    }

    /**
     * Returns the date the user was waitlisted.
     * @return Date waitlisted (timestamp).
     */
    @PropertyName("DateWaitlisted")
    public Long getDateWaitlisted() {
        return dateWaitlisted;
    }

    /**
     * Sets the date the user was waitlisted.
     * @param dateWaitlisted Date to set (timestamp).
     */
    @PropertyName("DateWaitlisted")
    public void setDateWaitlisted(Long dateWaitlisted) {
        this.dateWaitlisted = dateWaitlisted;
    }

    // Abstract methods below

    /**
     * Returns user information.
     * This method's implementation depends on the user type.
     * For example, a student would have a "major" variable while an admin would not.
     * @return User information.
     */
    public abstract String userInfo();

    /**
     * Returns a given user's department.
     * For a student, this would return their major, for a professor what department they are under, and for admin it would return as ADMIN.
     * Used for the TableView to populate the Department/Major column.
     * @return User's department (major, dept, or ADMIN depending on user type).
     */
    public abstract String getUserDept();
}