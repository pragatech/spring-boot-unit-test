package com.luv2code.springmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.Student;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class GradeBookControllerTest {

    private static MockHttpServletRequest request;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    StudentAndGradeService studentAndGradeServiceMock;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CollegeStudent collegeStudent;

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

    private static final MediaType APP_JSON_UTF8 = MediaType.APPLICATION_JSON;

    @Autowired
    private CollegeStudent student;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "praga");
        request.setParameter("lastname", "praga");
        request.setParameter("emailAddress", "praga@test.com");
    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    @Test
    public void getStudents() throws Exception {
        student.setFirstname("praga123");
        student.setLastname("ac123");
        student.setEmailAddress("samtest1@test.com");
        entityManager.persist(student);
        entityManager.flush();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void createStudent() throws Exception {
        student.setFirstname("praga");
        student.setLastname("ac");
        student.setEmailAddress("test@test.com");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("test@test.com");
        assertNotNull(verifyStudent);
    }

    @Test
    public void deleteStudent() throws Exception {
        assertTrue(studentDao.findById(100).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", 100))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));
        assertFalse(studentDao.findById(100).isPresent());
    }

    @Test
    public void deleteStudentInvalidStudentId() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void getStudentInformation() throws Exception {
        Optional<CollegeStudent> student = studentDao.findById(100);
        assertTrue(student.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 100))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")));

    }

    @Test
    public void getStudentInformationInvalidStudentId() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void createAValidGrade() throws Exception{
        assertTrue(studentDao.findById(100).isPresent());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade","90.00")
                .param("gradeType", "math")
                .param("studentId", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));
    }

    @Test
    public void createAValidGradeWithNotValidStudent() throws Exception{
        assertFalse(studentDao.findById(0).isPresent());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade","90.00")
                        .param("gradeType", "math")
                        .param("studentId", "0"))
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void createGradeForGradeTypeDoesNotExist() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "87.00")
                .param("gradeType", "qqq")
                .param("studentId", "100"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.status",is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void deleteAValidGrade() throws Exception{
        Optional<MathGrade> mathGrade = mathGradeDao.findById(101);
        assertTrue(mathGrade.isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 101, "math"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.id",is(100)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));
        mathGrade = mathGradeDao.findById(0);
        assertFalse(mathGrade.isPresent());
    }

    @Test
    public void deleteGradeNotValidGradeID() throws Exception{
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1000);
        assertFalse(mathGrade.isPresent());

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1000, "math"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void deleteGradeNotGradeType() throws Exception{
        Optional<MathGrade> mathGrade = mathGradeDao.findById(101);
        assertTrue(mathGrade.isPresent());
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 101,"qwerty" ))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(APP_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
}