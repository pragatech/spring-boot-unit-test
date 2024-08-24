package com.luv2code.component;

import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
//@SpringBootTest(classes = MvcTestingExampleApplication.class) - required when we are
// having different package
class ApplicationExampleTest {

    private static int count = 0;

    @Value("${info.app.name}")
    private String appInfo;

    @Value("${info.app.description}")
    private String appDesc;

    @Value("${info.app.version}")
    private String appVersion;

    @Value("${info.school.name}")
    private String appSchoolName;

    @Autowired
    CollegeStudent collegeStudent;

    @Autowired
    StudentGrades studentGrades;

    @Autowired
    ApplicationContext applicationContext;

    @BeforeEach
    public void beforeEach(){
        count = count+1;
        System.out.println("Testing: "+appInfo+ " which is "
            + appDesc + " Version: "+appVersion+". Execution " +
                "of Test method "+count
        );
        collegeStudent.setFirstname("Praga");
        collegeStudent.setLastname("AC");
        collegeStudent.setEmailAddress("acp2010@gmail.com");
        studentGrades.setMathGradeResults(new ArrayList<>(
                Arrays.asList(100.0, 85.0, 76.5, 91.75)));
        collegeStudent.setStudentGrades(studentGrades);
    }

    @DisplayName("Add Grade results for student grades")
    @Test
    public void addGradeResultsForStudentGrades(){
        assertEquals(353.25, studentGrades.addGradeResultsForSingleClass(
                collegeStudent.getStudentGrades().getMathGradeResults()
        ));
    }

    @DisplayName("Add Grade results for student grades not equal")
    @Test
    public void addGradeResultsForStudentGradesNotEqual(){
        assertNotEquals(0, studentGrades.addGradeResultsForSingleClass(
                collegeStudent.getStudentGrades().getMathGradeResults()
        ));
    }

    @DisplayName("Is Grade Greater")
    @Test
    public void isGradeGrater(){
        assertTrue(studentGrades.isGradeGreater(90, 75), "failure - Should be True");
    }

    @DisplayName("Is Grade Greater")
    @Test
    public void isNotGradeGrater(){
        assertFalse(studentGrades.isGradeGreater(75, 90), "failure - Should be false");
    }

    @DisplayName("check null")
    @Test
    public void checkNullForStudentGrades(){
        assertNotNull(studentGrades.checkNull(collegeStudent.getStudentGrades().getMathGradeResults()),
                "object Should not be null");
    }

    @DisplayName("Create Student without Grade Init")
    @Test
    public void createStudentWithoutGradeInit(){
        CollegeStudent studentTwo = applicationContext.getBean("collegeStudent", CollegeStudent.class);
        studentTwo.setFirstname("Praga");
        studentTwo.setLastname("AC");
        studentTwo.setEmailAddress("praga.test@gmail.com");
        assertNotNull(studentTwo.getFirstname());
        assertNotNull(studentTwo.getLastname());
        assertNotNull(studentTwo.getEmailAddress());
        assertNull(studentGrades.checkNull(studentTwo.getStudentGrades()));
    }

    @DisplayName("Students are prototyped")
    @Test
    public void verifyStudentsArePrototypes(){
        CollegeStudent studentTwo = applicationContext.getBean("collegeStudent", CollegeStudent.class);
        assertNotSame(studentTwo, collegeStudent);
    }

    @DisplayName("grade point average for student")
    @Test
    public void findGradePointAverage(){
        assertAll("Testing all assertEquals",
                () -> assertEquals(353.25, studentGrades.addGradeResultsForSingleClass(
                        collegeStudent.getStudentGrades().getMathGradeResults())),
                () -> assertEquals(88.31, studentGrades.findGradePointAverage(
                        collegeStudent.getStudentGrades().getMathGradeResults()))
        );
    }
}
