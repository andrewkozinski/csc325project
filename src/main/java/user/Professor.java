package user;

/**
 * Class which stores data of a user of the professor type
 *
 */
public class Professor extends User{

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
        super();
        this.department = department;
    }

    /**
     * Returns a string containing information about the user
     * @return String containing user information
     */
    @Override
    public String userInfo() {
        return "";
    }
}
