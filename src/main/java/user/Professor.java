package user;

/**
 * Class which stores data of a user of the professor type
 * Contains information about a professor
 * Including but not limited to: department, name, etc.
 * @author Andrew Kozinski
 */
public class Professor extends User{

    //Department a Professor is apart of
    private String department;

    //Default constructor
    /**
     * Default constructor, just sets variable values to default (null)
     */
    public Professor() {
        super();
        department = "null";
    }

    //Parameterized constructor
    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding user variables
     * @param username Passed in username
     * @param password Passed in password
     * @param firstName Passed in first name
     * @param lastName Passed in last name
     * @param userId Passed in user id
     * @param department Passed in department
     */
    public Professor(String username, String password, String firstName, String lastName, String userId, String department) {
        super(username, password, firstName, lastName, userId);
        this.department = department;
    }

    /**
     * Gets a professors department
     * @return Department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set a professors department
     * @param department Department to be set to
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Returns a string containing information about the user
     * @return String containing user information
     */
    @Override
    public String userInfo() {
        return String.format("Username: %s UserID: %s Department: %s", username, userId, department);
    }

    /**
     * Returns a given users department. For a professor this returns what department they are classified under.
     * @return The department a professor works under
     */
    @Override
    public String userDept() {
        return department;
    }
}
