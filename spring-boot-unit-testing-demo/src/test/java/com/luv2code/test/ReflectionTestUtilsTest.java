package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class ReflectionTestUtilsTest {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CollegeStudent collegeStudent;

    @Autowired
    StudentGrades studentGrades;

    @BeforeEach
    public void studentBeforeEach(){
        collegeStudent.setFirstname("praga");
        collegeStudent.setLastname("praga");
        collegeStudent.setEmailAddress("praga@gmail.com");
        collegeStudent.setStudentGrades(studentGrades);

        ReflectionTestUtils.setField(collegeStudent, "id", 1);
        ReflectionTestUtils.setField(collegeStudent, "studentGrades",
                new StudentGrades(new ArrayList<>(Arrays.asList(
                        100.0,85.0,76.5,91.75
                ))));
    }

    @Test
    public void getPrivateField(){
        assertEquals(1, ReflectionTestUtils.getField(collegeStudent, "id"));
    }

    @Test
    public void getPrivateMethod(){
        assertEquals("praga 1",
                ReflectionTestUtils.invokeMethod(collegeStudent, "getFirstNameAndId"));
    }
}
