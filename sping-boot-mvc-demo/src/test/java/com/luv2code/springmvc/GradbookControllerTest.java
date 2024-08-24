package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@SpringBootTest
public class GradbookControllerTest {
    @Autowired
    private static MockHttpServletRequest mockHttpServletRequest;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

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
    @Autowired
    private MathGradeDao mathGradeDao;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
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
    public void getStudentHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("Praga", "test",
                "pac@test.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("Praga1", "test1",
                "pac1@test.com");
        List<CollegeStudent> collegeStudents = new ArrayList<>(
                Arrays.asList(studentOne, studentTwo)
        );

        //when(studentAndGradeService.getGradeBook()).thenReturn(collegeStudents);
        //assertIterableEquals(collegeStudents, studentAndGradeService.getGradeBook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

    @BeforeAll
    public static void setupOnce(){
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("firstname", "iswarya");
        mockHttpServletRequest.setParameter("lastname", "test");
        mockHttpServletRequest.setParameter("emailAddress", "iswarya@test.com");
    }

    @Test
    public void createStudentHttprequest() throws Exception {
        CollegeStudent studentOne = new CollegeStudent(
                "sam", "praga", "test@sam.com"
        );
        List<CollegeStudent> collegeStudents = new ArrayList<>(
                Arrays.asList(studentOne)
        );
        //when(studentAndGradeService.getGradeBook()).thenReturn(collegeStudents);
        //assertIterableEquals(collegeStudents, studentAndGradeService.getGradeBook());

        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstname", "iswarya")
                        .param("lastname", "test")
                        .param("emailAddress", "iswarya@test.com"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent student = studentDao.findByEmailAddress("iswarya@test.com");
        assertNotNull(student, "student should be found");
    }

    @Test
    public void testDeleteStudent() throws Exception{
        assertTrue(studentDao.findById(101).isPresent(), "return true");
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/101"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "index");
        assertFalse(studentDao.findById(101).isPresent(), "return False");
    }

    @Test
    public void testDeleteStudentHTTPerrorPage() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.delete("/0"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void studentInformationHttpRequest() throws Exception{
        assertTrue(studentDao.findById(101).isPresent());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/studentInformation/{id}", 101))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"studentInformation");
    }

    @Test
    public void studentInformationErrorPage() throws Exception{
        assertFalse(studentDao.findById(100).isPresent());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/studentInformation/{id}", 100))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }

    @Test
    public void createValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(101).isPresent());
        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.getStudentInformation(101);
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "101"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"studentInformation");

        gradebookCollegeStudent = studentAndGradeService.getStudentInformation(101);
        assertEquals(2, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());

    }

    @Test
    public void createValidGradeHttpRequestWhenStudentDoesnotExist() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "history")
                        .param("studentId", "100")
        ).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void createANonValidGradeHttpRequestGradeTypeDoesNotExistEmptyResponse() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade","90.99")
                        .param("gradeType","qwe")
                        .param("studentId", "101")
        ).andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }

    @Test
    public void deleteAValidGradeHttpRequest() throws Exception{
        Optional<MathGrade> grade = mathGradeDao.findById(101);
        assertTrue(grade.isPresent());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 101,"math")
        ).andExpect(status().isOk()).andReturn();

        ModelAndView view = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(view, "studentInformation");

        grade = mathGradeDao.findById(101);
        assertFalse(grade.isPresent());
    }

    @Test
    public void deleteAValidGradeHttpRequestStudentIdDoesNotExistEmployeeResponse() throws Exception{
        Optional<MathGrade> mathGrade = mathGradeDao.findById(102);
        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 102, "math"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }

    @Test
    public void deleteANonValidHttpRequest() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 101, "qwe"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }
}
