package course;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void getAndSetCurrentWaitlistCount() {
        Course course = new Course();
        course.setCurrentWaitlistCount(1);
        assertEquals(1, course.getCurrentWaitlistCount());
    }

    @Test
    void getAndSetCourseCRN() {
        Course course = new Course();
        course.setCourseCRN("99999");
        assertEquals("99999", course.getCourseCRN());
    }

    @Test
    void incrementEnrolledCount() {
        Course course = new Course();
        course.setCapacity(100);
        for (int i = 0; i < 90; i++) {
            course.incrementEnrolledCount();
        }
        assertEquals(90, course.getCurrentEnrolledCount());
    }

    @Test
    void decrementEnrolledCount() {
        Course course = new Course();
        course.setCapacity(90);
        course.setCurrentEnrolledCount(90);
        for (int i = 0; i < 90; i++) {
            course.decrementEnrolledCount();
        }
        assertEquals(0, course.getCurrentEnrolledCount());
    }

}