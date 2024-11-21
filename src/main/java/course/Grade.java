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
     * Parameterized constructor, takes in values and sets them
     * @param course The Course instance passed in
     * @param student The Student instance
     * @param grade The grade passed in
     * @param letterGrade The letter grade passed in
     */
    public Grade(Course course, Student student, double grade, String letterGrade) {
        this.course = course;
        this.student = student;
        this.grade = grade;
        this.letterGrade = letterGrade;
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

        //Just automatically set the letter grade
        //Uses helper method written below
        this.letterGrade = convertToLetterGrade(grade);
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

    /**
     * Helper method to convert a given double into a letter grade
     * Made public in case this method was to be used anywhere else
     * If needed to be used for whatever reason outside a Grade instance, feel free to make static -Andrew
     * @param grade Grade passed in
     * @return String of the grade
     */
    public String convertToLetterGrade(double grade) {
        //Just handled this logic as a simple if else block
        //Feel free to adjust this in case we wanted to add grades like B-, B+, etc.
        if(grade >= 90) {
            return "A";
        }
        else if(grade >= 80) {
            return "B";
        }
        else if(grade >= 70) {
            return "C";
        }
        else if(grade >= 60) {
            return "D";
        }
        else {
            return "F";
        }
    }
}
