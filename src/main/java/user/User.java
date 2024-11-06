package user;

import java.util.Date;

/**
 * An abstract class representing a user in the registration system
 * Users can include but are not limited to: Student, Professor and Admin
 * Stores information regarding the user such as username, id, etc.
 * Various methods with default behavior of a user are defined below.
 * @author Andrew Kozinski
 */
public abstract class User {

    //User variables
    protected String username;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String userId;

    //Default constructor
    /**
     * Default constructor, just sets variable values to default (null)
     */
    public User() {
        username = "Null";
        password = "Null";
        firstName = "Null";
        lastName = "Null";
        userId = "Null";
    }
    //Parameterized Constructor

    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding user variables
     * @param username Passed in username
     * @param password Passed in password
     * @param firstName Passed in firstname
     * @param lastName Passed in lastname
     * @param userId Passed in userId
     */
    public User(String username, String password, String firstName, String lastName, String userId) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
    }


    //Default implementation of User methods below

    /**
     * Returns username upon call
     * @return username
     */
    public String getUserName() {
        return username;
    }

    /**
     * Sets username upon call
     * @param userName username we wish to set
     */
    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Gets user id of a user
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id of a user
     * @param userId user id of a given user
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the first name of a user
     * @return first name of a user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of a user
     * @param firstName first name to be set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of a user
     * @return Last name of a given user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of a user
     * @param lastName last name to be set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    //Abstract methods below

    /**
     * Returns user information
     * This methods implementation depends on the user type
     * For example, a student would have a "major" variable while an admin would not
     * @return User information
     */
    public abstract String userInfo();

    /**
     * Returns a given users department. For a student, this would return their major, for a professor what department they are under, and for admin it would return as ADMIN.
     * @return User department (either major, dept, or admin depending on usertype)
     */
    public abstract String userDept();

}
