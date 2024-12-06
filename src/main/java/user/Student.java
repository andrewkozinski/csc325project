package user;

/**
 * Class which stores data of a user of the student type
 * Contains information about a student
 * Including but not limited to: Major, Classification, name, etc.
 * @author Andrew Kozinski
 */
public class Student extends User {

    //Student Major
    private String Major;
    //Classification refers to what year a student is
    //Example: Senior
    private String Classification;
    private String dateWaitlistedString;
    private boolean twoFactorEnabled;

    //Default constructor
    /**
     * Default constructor, just sets variable values to default (null)
     */
    public Student() {
        super();
        Classification = "null";
        Major = "null";
    }

    //Parametrized Constructor
    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding user variables
     * @param username Passed in Username
     * @param password Passed in password
     * @param firstName Passed in first name
     * @param lastName Passed in last name
     * @param userId Passed in user id
     * @param classification Passed in student Classification
     * @param major Passed in Major
     */
    public Student(String username, String password, String firstName, String lastName, String userId, String age, String classification, String major) {
        super(username, password, firstName, lastName, userId, age);
        this.Classification = classification;
        this.Major = major;
    }

    //Method definitions:

    /**
     * Gets a students Major
     * @return A students Major
     */
    public String getMajor() {
        return Major;
    }

    /**
     * Sets a students Major
     * @param major Student Major passed in
     */
    public void setMajor(String major) {
        this.Major = major;
    }

    /**
     * Gets the Classification of a student
     * @return The Classification of a student
     */
    public String getClassification() {
        return Classification;
    }

    /**
     * Sets the Classification of a student
     * @param classification given Classification of a student
     */
    public void setClassification(String classification) {
        this.Classification = classification;
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
        return String.format("Username: %s UserID: %s Major: %s Classification: %s", Username, UserId, Major, Classification);
    }

    /**
     * Returns a given users department. For a student, this would return their Major.
     * @return A student's Major
     */
    @Override
    public String getUserDept() {
        return Major;
    }
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

}
