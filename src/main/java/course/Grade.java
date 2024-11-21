package course;

import user.Student;

/**
 * Grade class used for ProfessorCourseGrades to associate a student object with a grade
 */
public class Grade {

    //Course a grade is associated with
    private Course course;
    //Student a grade is associated
    private Student student;
    //Grade a student has
    private double grade;
    //String to be returned for letter grade implementation
    private String letterGrade;

    /**
     * Default constructor, initializes values to default
     */
    public Grade() {
        grade = 0;
        letterGrade = "F";
        student = new Student();
        course = new Course();
    }

    /**
     * Get the course a grade is associated with
     * @return A course instance
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Set the course that a grade is associated with
     * @param course Course to be set
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * Gets the student that a grade is associated with
     * @return Student instance
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Sets the student that a grade is associated with
     * @param student Student object passed in
     */
    public void setStudent(Student student) {
        this.student = student;
    }

    /**
     * Gets the grade (numeric)
     * @return Grade
     */
    public double getGrade() {
        return grade;
    }

    /**
     * Sets the grade
     * @param grade Grade to be set
     */
    public void setGrade(double grade) {
        this.grade = grade;

        //Calculating letter grade logic may be put here in the future,
        //Haven't done so yet as I didn't feel like doing it yet
    }

    /**
     * Gets the letter grade
     * @return Letter grade a student has
     */
    public String getLetterGrade() {
        return letterGrade;
    }

    /**
     * Sets the letter grade
     * @param letterGrade Letter grade to be set
     */
    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }
}
