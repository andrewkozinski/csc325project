package course;

import user.Professor;

/**
 * Class which stores data of a given course
 * @author Andrew Kozinski
 */
public class Course {
    //CRN number
    private String courseCRN;
    //Courses name, example: Software Engineering
    private String courseName;
    //Course code example: CSC 325
    private String courseCode;
    //Days a course meets
    private String courseDays;
    //Room the course meets in
    private String courseLocation;
    //num of credits a course is worth
    private int credits;
    //Professor teaching a given course
    private Professor professor;
    //The amount of students a given course can have
    private int capacity;
    //Amount of students currently enrolled for the course
    private int currentEnrolledCount;

    /**
     * Default constructor, sets variables to default values
     */
    public Course() {
        courseCRN = "null";
        courseName = "null";
        courseCode = "null";
        courseDays = "null";
        courseLocation = "null";
        credits = 0;
        professor = new Professor();
        capacity = 0;
        currentEnrolledCount = 0;
    }

    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding course variables
     * @param classCRN Course CRN
     * @param courseName Course name
     * @param courseCode Course code
     * @param courseDays Days a course meets (ex: Monday/Wednesday)
     * @param courseLocation Room a course meets
     * @param credits Number of credits a course is worth
     * @param professor Professor teaching a course
     */
    public Course(String classCRN, String courseName, String courseCode, String courseDays, String courseLocation, int credits, Professor professor, int capacity, int currentEnrolledCount) {
        this.courseCRN = classCRN;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseDays = courseDays;
        this.courseLocation = courseLocation;
        this.credits = credits;
        this.professor = professor;
        this.capacity = capacity;
        this.currentEnrolledCount = currentEnrolledCount;
    }

    /**
     * Returns a course's CRN
     * @return A given course's CRN
     */
    public String getCourseCRN() {
        return courseCRN;
    }

    /**
     * Sets the course CRN
     * @param courseCRN Given course CRN
     */
    public void setCourseCRN(String courseCRN) {
        this.courseCRN = courseCRN;
    }

    /**
     * Gets a course's name
     * @return Course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets a course name
     * @param courseName Given course name
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Gets a course code (Ex: CSC325)
     * @return Returns a course's code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Sets a course's code
     * @param courseCode Given course code
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /**
     * Gets the days a course meets
     * @return Days a course meets
     */
    public String getCourseDays() {
        return courseDays;
    }

    /**
     * Sets the days a course meets
     * @param courseDays Sets the days a course meets
     */
    public void setCourseDays(String courseDays) {
        this.courseDays = courseDays;
    }

    /**
     * Gets the room (location) a course meets
     * @return Room a course meets
     */
    public String getCourseLocation() {
        return courseLocation;
    }

    /**
     * Sets the location a course meets
     * @param courseLocation Given course location
     */
    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    /**
     * Gets the amount of credits a course is worth
     * @return Credits a course is worth
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Sets the amount of credits a course is worth
     * @param credits Given amount of credits a course is worth
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * Gets the Professor teaching a course
     * @return Professor teaching a course
     */
    public Professor getProfessor() {
        return professor;
    }

    /**
     * Sets the Professor teaching a course
     * @param professor Inputted Professor
     */
    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    /**
     *
     * @return
     */
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentEnrolledCount() {
        return currentEnrolledCount;
    }

    public void setCurrentEnrolledCount(int currentEnrolledCount) {
        this.currentEnrolledCount = currentEnrolledCount;
    }

}
