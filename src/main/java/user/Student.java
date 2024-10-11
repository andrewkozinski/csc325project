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
    }

    //Parametrized Constructor

    /**
     *
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @param userId
     * @param classification
     */
    public Student(String username, String password, String firstName, String lastName, String userId, String classification) {
        super(username, password, firstName, lastName, userId);
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
