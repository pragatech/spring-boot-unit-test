package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;
    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;
    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;
    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;
    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;
    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;
    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void insertSampleData(){
        jdbcTemplate.execute(sqlAddStudent);
        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddHistoryGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
    }

    @AfterEach
    public void deleteSampleData(){
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
    }

    @Test
    public void createStudentService(){
        studentService.createStudent("praga","ac","test@gmail.com");
        CollegeStudent student = studentDao.findByEmailAddress("test@gmail.com");
        assertEquals("test@gmail.com",student.getEmailAddress(),
                "find by email");
    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(101));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudent(){
        Optional<CollegeStudent> deleteCollegeStudent = studentDao.findById(101);
        Optional<MathGrade> deleteMathGrade = mathGradeDao.findById(101);
        Optional<HistoryGrade> deletehistoryGrade = historyGradeDao.findById(101);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradeDao.findById(101);

        assertTrue(deleteCollegeStudent.isPresent(), "Return True");
        assertTrue(deleteMathGrade.isPresent(), "Return True");
        assertTrue(deletehistoryGrade.isPresent(), "Return True");
        assertTrue(deleteScienceGrade.isPresent(), "Return True");

        studentService.deleteStudent(101);

        deleteCollegeStudent = studentDao.findById(1);
        deleteMathGrade = mathGradeDao.findById(101);
        deletehistoryGrade = historyGradeDao.findById(101);
        deleteScienceGrade = scienceGradeDao.findById(101);

        assertFalse(deleteCollegeStudent.isPresent(), "Return False");
        assertFalse(deleteMathGrade.isPresent(), "Return False");
        assertFalse(deletehistoryGrade.isPresent(), "Return False");
        assertFalse(deleteScienceGrade.isPresent(), "Return False");
    }

    @Sql("/initData.sql")
    @Test
    public void getGradebookService(){
        Iterable<CollegeStudent> iterableCollegestudents = studentService.getGradeBook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for(CollegeStudent student : iterableCollegestudents){
            collegeStudents.add(student);
        }
        assertEquals(6, collegeStudents.size());
    }


    @Test
    public void createGradeService(){
        //Create the Grade
        assertTrue(studentService.createGrade(80.5, 101, "math"));
        assertTrue(studentService.createGrade(80.5, 101, "science"));
        assertTrue(studentService.createGrade(80.5, 101, "history"));
        //Get All grades with student ID
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(101);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(101);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(101);
        //verify there is grades
        assertTrue(mathGrades.iterator().hasNext(), "Student has math grade");
        assertTrue(scienceGrades.iterator().hasNext(), "Student has science grade");
        assertTrue(historyGrades.iterator().hasNext(), "Student has history grade");

        assertTrue(((Collection<MathGrade>)mathGrades).size() ==2, "Student has math grade");
        assertTrue(((Collection<ScienceGrade>)scienceGrades).size() ==2, "Student has math grade");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has math grade");
    }

    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5,1,"math"));
        assertFalse(studentService.createGrade(80.5,4, "math"));
        assertFalse(studentService.createGrade(89, 1, "test"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(101, studentService.deleteGrade(101, "math"),
                "Returns student ID");
        assertEquals(101, studentService.deleteGrade(101, "science"),
                "Returns student ID");
        assertEquals(101, studentService.deleteGrade(101, "history"),
                "Returns student ID");
    }

    @Test
    public void deleteGradeServiceReturnFalse(){
        assertEquals(0, studentService.deleteGrade(1, "math"),
                "Returns student ID");
        assertEquals(0, studentService.deleteGrade(1, "science"),
                "Returns student ID");
        assertEquals(0, studentService.deleteGrade(1, "history"),
                "Returns student ID");
        assertEquals(0, studentService.deleteGrade(101, "test"),
                "Returns student ID");
    }

    @Test
    public void testStudentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.getStudentInformation(101);
        assertNotNull(gradebookCollegeStudent);
        assertEquals(101, gradebookCollegeStudent.getId());
        assertEquals("Praga", gradebookCollegeStudent.getFirstname());
        assertEquals("test", gradebookCollegeStudent.getLastname());
        assertEquals("pac@test.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
    }


    @Test
    public void testStudentInformationReturnNull(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.getStudentInformation(0);
        assertNull(gradebookCollegeStudent);
    }
}
