package user;

/**
 * Class which stores data of a user of the admin type
 * Contains information about an admin
 * Including but not limited to: userid, username, etc.
 * @author Andrew Kozinski
 */
public class Admin extends User{

    //Default constructor
    /**
     * Default constructor, just sets variable values to default (null)
     */
    public Admin() {
        super();
    }

    //Parameterized constructor
    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding user variables
     * @param username Passed in username
     * @param password Passed in password
     * @param firstName Passed in first name
     * @param lastName Passed in last name
     * @param userId Passed in user id
     */
    public Admin(String username, String password, String firstName, String lastName, String userId, String age) {
        super(username, password, firstName, lastName, userId, age);
    }

    /**
     * Returns a string containing information about the user
     * @return String containing user information
     */
    @Override
    public String userInfo() {
        return userId;
    }

    /**
     * Returns the department of the user.
     * In this case, would simply be admin in all cases.
     * @return Admin "department"
     */
    @Override
    public String getUserDept() {
        return "ADMIN";
    }

}
