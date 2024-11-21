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
        Long dateWaitlisted = Long.valueOf("1732040571548");
        //String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(dateWaitlisted));
        student.setDateWaitlisted(dateWaitlisted);
        assertEquals(dateWaitlisted, student.getDateWaitlisted());
    } //Figure this out later



}