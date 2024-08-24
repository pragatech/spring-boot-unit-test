package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
class MockAnnotationTest {
    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent student;

    @Autowired
    StudentGrades grades;

    @MockBean
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StudentGrades studentGrades;

    @BeforeEach
    public void beforeEach(){
        student.setFirstname("Praga");
        student.setLastname("AC");
        student.setEmailAddress("test@gmail.com");
        student.setStudentGrades(studentGrades);
    }

    @DisplayName("when & verify")
    @Test
    public void assertEqualsTestAddGrades(){
        when(applicationDao.addGradeResultsForSingleClass(
                student.getStudentGrades().getMathGradeResults()
        )).thenReturn(100.0);
        assertEquals(100,
                applicationService.addGradeResultsForSingleClass(
                        student.getStudentGrades().getMathGradeResults()
                ));
        verify(applicationDao)
                .addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults());
        verify(applicationDao, times(1))
                .addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults());
    }

    @DisplayName("Finding GPA ")
    @Test
    public void assertEqualsTestFindGpa(){
        when(applicationDao.findGradePointAverage(
                student.getStudentGrades().getMathGradeResults())).thenReturn(88.31);
        assertEquals(88.31, applicationService.findGradePointAverage(
                student.getStudentGrades().getMathGradeResults()
        ));
        verify(applicationDao,times(1)).findGradePointAverage(
                student.getStudentGrades().getMathGradeResults()
        );
    }

    @DisplayName("Not Null")
    @Test
    public void testAssertNotNull(){
        when(applicationDao.checkNull(studentGrades.getMathGradeResults())).thenReturn(true);
        assertNotNull(applicationService.checkNull(student.getStudentGrades().getMathGradeResults()),
                "Object should not be null");
        verify(applicationDao, times(1)).checkNull(studentGrades.getMathGradeResults());
    }

    @DisplayName("Throw Exception")
    @Test
    public void testThrowException(){
        CollegeStudent nullStudent = context.getBean("collegeStudent", CollegeStudent.class);
        when(applicationDao.checkNull(nullStudent)).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    @DisplayName("Multiple Stubbing")
    @Test
    public void testMultipleStubbing(){
        CollegeStudent nullStudent = context.getBean("collegeStudent", CollegeStudent.class);
        when(applicationDao.checkNull(nullStudent))
                .thenThrow(new RuntimeException())
                .thenReturn("Do not throw exception on second Time");
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        assertEquals("Do not throw exception on second Time", applicationService.checkNull(nullStudent));
        verify(applicationDao, times(2)).checkNull(nullStudent);
    }
}
