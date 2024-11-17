package user;

import java.util.Date;

/**
 * Class which stores data of a user of the student type
 * Contains information about a student
 * Including but not limited to: major, classification, name, etc.
 * @author Andrew Kozinski
 */
public class Student extends User {

    //Student major
    private String major;
    //Classification refers to what year a student is
    //Example: Senior
    private String classification;
    private String dateWaitlistedString;

    //Default constructor
    /**
     * Default constructor, just sets variable values to default (null)
     */
    public Student() {
        super();
        classification = "null";
        major = "null";
    }

    //Parametrized Constructor
    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding user variables
     * @param username Passed in username
     * @param password Passed in password
     * @param firstName Passed in first name
     * @param lastName Passed in last name
     * @param userId Passed in user id
     * @param classification Passed in student classification
     * @param major Passed in major
     */
    public Student(String username, String password, String firstName, String lastName, String userId, String age, String classification, String major) {
        super(username, password, firstName, lastName, userId, age);
        this.classification = classification;
        this.major = major;
    }

    //Method definitions:

    /**
     * Gets a students major
     * @return
     */
    public String getMajor() {
        return major;
    }

    /**
     * Sets a students major
     * @param major
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * Gets the classification of a student
     * @return The classification of a student
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the classification of a student
     * @param classification given classification of a student
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }
    public String getDateWaitlistedString() {
        return dateWaitlistedString;
    }

    public void setDateWaitlistedString(String dateWaitlistedString) {
        this.dateWaitlistedString = dateWaitlistedString;
    }

    /**
     * Returns a string containing information about the user
     * @return String containing user information
     */
    @Override
    public String userInfo() {
        return String.format("Username: %s UserID: %s Major: %s Classification: %s", username, userId, major, classification);
    }

    /**
     * Returns a given users department. For a student, this would return their major.
     * @return A student's major
     */
    @Override
    public String getUserDept() {
        return major;
    }

}
