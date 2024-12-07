# Atlantis University Registration System Overview:

#### This system provides functionality for students, professors, and administrators to manage course enrollments, grades, user accounts, and course information. It supports various user roles and provides features such as course enrollment, viewing grades, managing course information, user account management, two-factor authentication, and more.
---
## Table of Contents
1. **[Features](#features)**
   - 1.1. [Enrolling and Unenrolling in Courses](#enrolling-and-unenrolling-in-courses)
   - 1.2. [Viewing Grades](#viewing-grades)
   - 1.3. [Manage Course Information](#manage-course-information)
   - 1.4. [Manage User Accounts](#manage-user-accounts)
   - 1.5. [Create and Delete Courses](#create-and-delete-courses)
   - 1.6. [Accessing Student Schedule](#accessing-student-schedule)
   - 1.7. [Updating Grades](#updating-grades)
   - 1.8. [Modifying & Viewing Course Section Information](#modifying--viewing-course-section-information)
   - 1.9. [Waitlist Functionality](#waitlist-functionality)
   - 1.10. [User Settings](#user-settings)
   - 1.11. [Two-Factor Authentication](#two-factor-authentication)
---
## Features
Below is a general overview of some of the features. This is not a completely comprehensive list.
### 1.1 Enrolling and Unenrolling in Courses
- **Description**: Students can enroll or unenroll from courses based on availability. They can register for courses that are not at full capacity, and unenroll from courses they are currently registered in.
    - Students can view available courses to register for.
    - Students can enroll in courses if available space exists.
    - If space does not exist in a course, upon registering the student will be added to that course's waitlist.
    - Students can drop courses they are enrolled in.

### 1.2 Viewing Grades

- **Description**: Students can view their grades for courses they are enrolled in.
    - The system retrieves and displays grades from Firestore for a course they are enrolled in.
  
### 1.3 Manage Course Information

- **Description**: Admin users can alter course details such as course descriptions, class sizes, and credit hours.
    - Administrators can update course information.

### 1.4 Manage User Accounts

- **Description**: Administrators can create, update, and manage user accounts.
    - Administrators can create new accounts and assign roles.
    - Administrators can update existing user account information.

### 1.5 Create and Delete Courses

- **Description**: Administrators can create and delete courses, and assign professors to course sections.
    - Administrators can create courses with descriptions, credit hours, and assign a Professor to that course.
    - Administrators can delete courses.

### 1.6 Accessing Student Schedule

- **Description**: Students can view their course schedule.
    - Students can view their schedules which list out all courses a student is enrolled in.

### 1.7 Updating Grades

- **Description**: Professors can view and update student grades for courses they are assigned to.
    - Professors can view and modify grades for students in their courses.

### 1.8 Modifying & Viewing Course Section Information

- **Description**: Professors can modify required textbooks and view students enrolled into their course sections.
    - Professors can update the required textbook for a course they are assigned to.
    - Professors can view course enrollment information.

### 1.9 Waitlist Functionality

- **Description**: If a course is full, students are added to a waitlist and can be enrolled if space becomes available.
    - Students are added to the waitlist if they attempt to enroll in a full course.
    - If a student drops a course, the student at the front of the waitlist is enrolled.
    - Relevant updates are made in the system (Firebase).

### 1.10 Account Information

- **Description**: Users on the account information page can change their password and enable and disable two-factor authentication.
    - Users can change their password.
    - Users can enable and disable two-factor authentication.

### 1.11 Two-Factor Authentication

- **Description**: Users with two-factor authentication enabled will be prompted for a verification code during login.
    - Users are prompted to enter a verification code if two-factor authentication is enabled.
