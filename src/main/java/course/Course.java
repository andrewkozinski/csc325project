package course;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import user.Professor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which stores data of a given course
 * @author Andrew Kozinski
 */
public class Course {
    // Mappings due to variable case sensitivity
    //CRN number
    private String courseCRN;
    //Courses name, example: Software Engineering
    private String courseName;
    //Course code example: CSC 325
    private String courseCode;
    //Days a course meets
    private String courseDays;
    //Time a course meets
    private String courseTime;
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
    //A courses description
    private String courseDescription;
    //The textbook required by a given course, if a course does not have a textbook can be set to null or none or something along those lines
    private String courseTextbook;
    private DocumentReference professorReference;
    private final int waitlistCap = 10;
    private int currentWaitlistCount;
    private List<String> enrolledStudents;
    private List<Map<String, Object>> waitlistedStudents;
    private final StringProperty professorName;

    /**
     * Default constructor, sets variables to default values
     */
    public Course() {
        courseCRN = "null";
        courseName = "null";
        courseCode = "null";
        courseDays = "null";
        courseTime = "null";
        courseLocation = "null";
        credits = 0;
        professor = new Professor();
        capacity = 0;
        currentEnrolledCount = 0;
        courseDescription = "null";
        courseTextbook = "null";
        currentWaitlistCount = 0;
        enrolledStudents = new ArrayList<>();
        waitlistedStudents = new ArrayList<>();
        this.professorName = new SimpleStringProperty();
    }

    /**
     * Parameterized constructor, takes in variables and sets them to the corresponding course variables
     * @param classCRN Course CRN
     * @param courseName Course name
     * @param courseCode Course code
     * @param courseDays Days a course meets (ex: Monday/Wednesday)
     * @param courseTime Times a course meets (ex: 12:05-1:30pm)
     * @param courseLocation Room a course meets
     * @param credits Number of credits a course is worth
     * @param professor Professor teaching a course
     */
    public Course(String classCRN, String courseName, String courseCode, String courseDays, String courseTime, String courseLocation, int credits, Professor professor, int capacity, int currentEnrolledCount, String courseDescription, String courseTextbook, int waitlist, int currentWaitlistCount, List<Map<String, Object>> waitlistedStudents) {
        this.courseCRN = classCRN;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseDays = courseDays;
        this.courseTime = courseTime;
        this.courseLocation = courseLocation;
        this.credits = credits;
        this.professor = professor;
        this.capacity = capacity;
        this.currentEnrolledCount = currentEnrolledCount;
        this.courseDescription = courseDescription;
        this.courseTextbook = courseTextbook;
        this.currentWaitlistCount = currentWaitlistCount;
        this.enrolledStudents = (enrolledStudents != null) ? enrolledStudents : new ArrayList<>();
        this.waitlistedStudents = (waitlistedStudents != null) ? waitlistedStudents : new ArrayList<>();
        this.professorName = new SimpleStringProperty(); // Initialize the professorName property

        // Set the professor name if the professor is not null
        if (professor != null) {
            this.professorName.set(professor.getFirstName() + " " + professor.getLastName());
        }
    }


    public int getCurrentWaitlistCount() {
        return currentWaitlistCount;
    }
    public void setCurrentWaitlistCount(int currentWaitlistCount) {
        this.currentWaitlistCount = currentWaitlistCount;
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
     * Gets the time a course meets
     * @return Time a course meets
     */
    public String getCourseTime() {
        return courseTime;
    }

    /**
     * Sets the time a course meets
     * @param courseTime Time a course meets
     */
    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
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
     * Gets capacity of a course
     * @return Course capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets capacity of a course
     * @param capacity The capacity we're setting to
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Get the number of students currently enrolled for the course
     * @return Amount of students enrolled
     */
    public int getCurrentEnrolledCount() {
        return currentEnrolledCount;
    }

    /**
     * Sets the amount of students currently enrolled for the course
     * @param currentEnrolledCount Amount of students enrolled
     */
    public void setCurrentEnrolledCount(int currentEnrolledCount) {
        this.currentEnrolledCount = currentEnrolledCount;
    }

    /**
     * Sets a given course's description
     * @param description Course description to be set
     */
    public void setCourseDescription(String description) {
        this.courseDescription = description;
    }

    /**
     * Returns a course's description
     * @return Course description
     */
    public String getCourseDescription() {
        return courseDescription;
    }

    /**
     * Sets a course's required textbook
     * @param textbook Required textbook of a course
     */
    public void setCourseTextbook(String textbook) {
        this.courseTextbook = textbook;
    }

    /**
     * Returns the textbook a course requires
     * @return Textbook a course requires
     */
    public String getCourseTextbook() {
        return courseTextbook;
    }

    public List<String> getEnrolledStudents() {
        if (enrolledStudents == null) {
            enrolledStudents = new ArrayList<>();
        }
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public List<Map<String, Object>> getWaitlistedStudents() {
        if (waitlistedStudents == null) {
            waitlistedStudents = new ArrayList<>();
        }
        return waitlistedStudents;
    }

    public void setWaitlistedStudents(List<Map<String, Object>> waitlistedStudents) {
        if (waitlistedStudents != null) {
            for (Map<String, Object> entry : waitlistedStudents) {
                if (!entry.containsKey("details")) {
                    entry.put("details", new HashMap<String, Object>());
                }
            }
        }
        this.waitlistedStudents = waitlistedStudents;
    }
    public void addStudentToWaitlist(String studentId) {
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("studentUserId", studentId); // Changed to match Firestore field name
        waitlistEntry.put("DateAdded", FieldValue.serverTimestamp());
        getWaitlistedStudents().add(waitlistEntry);
    }

    /**
     * Increases amount of students enrolled by 1
     */
    public void incrementEnrolledCount() {
        //If the number of students enrolled is not at capacity we can add a student
        if(currentEnrolledCount < capacity) {
            currentEnrolledCount++;
        } else if (currentWaitlistCount < waitlistCap) {
            incrementWaitlistCount();
        }
    }

    /**
     * Decreases amount of students enrolled by 1
     */
    public void decrementEnrolledCount() {
        //If the number of students currently enrolled is not 0, we can go ahead and decrement
        if(currentEnrolledCount > 0) {
            currentEnrolledCount--;
        }
    }

    public DocumentReference getProfessorReference() {
        return professorReference;
    }
    public void setProfessorReference(DocumentReference professorReference) {
        this.professorReference = professorReference;
    }

    /**
     * Increments the current waitlist count if capacity is reached
     */
    public void incrementWaitlistCount() {
        if (currentWaitlistCount < waitlistCap) {
            currentWaitlistCount++;
        }
    }

    /**
     * Decreases the current waitlist count if possible
     */
    public void decrementWaitlistCount() {
        if (currentWaitlistCount > 0) {
            currentWaitlistCount--;
        }
    }
    public String getProfessorName() {
        return professorName.get();
    }

    public void setProfessorName(String professorName) {
        this.professorName.set(professorName);
    }

    public StringProperty professorNameProperty() {
        return professorName;
    }
}