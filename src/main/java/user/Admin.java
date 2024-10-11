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
    public Admin(String username, String password, String firstName, String lastName, String userId) {
        super(username, password, firstName, lastName, userId);
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
