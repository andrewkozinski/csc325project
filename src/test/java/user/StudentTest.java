package user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void getAndSetMajor() {
        Student student = new Student();
        student.setMajor("Music Theory");
        assertEquals("Music Theory", student.getMajor());
    }

    @Test
    void getAndSetClassification() {
        Student student = new Student();
        student.setClassification("Super Senior");
        assertEquals("Super Senior", student.getClassification());
    }

    @Test
    void getAndSetDateWaitlistedString() {
        Student student = new Student();
        student.setDateWaitlisted(Long.valueOf("43954982"));
        assertEquals("43954982", student.getDateWaitlisted());
    } //Figure this out later



}