package user;

import java.util.Date;

public class Student extends User {

    private String major;
    //Classification refers to what year a student is
    //Example: Senior
    private String classification;

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
    public Student(String username, String password, String firstName, String lastName, String userId, String classification, String major) {
        super(username, password, firstName, lastName, userId);
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
     * @return
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the classification of a student
     * @param classification
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

    /**
     * Returns a string containing information about the user
     * @return
     */
    @Override
    public String userInfo() {
        return String.format("Username: %s UserID: %s Major: %s Classification: %s", username, userId, classification);
    }
}
