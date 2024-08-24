package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scMathGradeDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;
    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;
    @Autowired
    private HistoryGradeDao historyGradeDao;
    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(
            String firstName,
            String lastName,
            String email
    ){
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id){
        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudent(int id){
        studentDao.deleteById(id);
        mathGradeDao.deleteByStudentId(id);
        scienceGradeDao.deleteByStudentId(id);
        historyGradeDao.deleteByStudentId(id);
    }

    public Iterable<CollegeStudent> getGradeBook(){
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if(!checkIfStudentIsNull(studentId)){
            return false;
        }

        if(grade >= 0 && grade <=100){
            if(gradeType.equalsIgnoreCase("math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDao.save(mathGrade);
                return true;
            }else if(gradeType.equalsIgnoreCase("science")){
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                scienceGradeDao.save(scienceGrade);
                return true;
            } else if (gradeType.equalsIgnoreCase("history")) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);
                historyGradeDao.save(historyGrade);
                return true;
            }
        }
        return false;
    }

    public int deleteGrade(int gradeId, String gradeType) {
        int studentId = 0;

        if(gradeType.equalsIgnoreCase("math")){
            Optional<MathGrade> grade = mathGradeDao.findById(gradeId);
            if(!grade.isPresent()){
                return studentId;
            }
            studentId = grade.get().getStudentId();
            mathGradeDao.deleteById(gradeId);
        } else if (gradeType.equalsIgnoreCase("science")) {
            Optional<ScienceGrade> grade = scienceGradeDao.findById(gradeId);
            if(!grade.isPresent()){
                return studentId;
            }
            studentId = grade.get().getStudentId();
            scienceGradeDao.deleteById(gradeId);
        } else if (gradeType.equalsIgnoreCase("history")) {
            Optional<HistoryGrade> grade = historyGradeDao.findById(gradeId);
            if(!grade.isPresent()){
                return studentId;
            }
            studentId = grade.get().getStudentId();
            historyGradeDao.deleteById(gradeId);
        }
        return studentId;
    }

    public GradebookCollegeStudent getStudentInformation(int studentId) {
        Optional<CollegeStudent> student = studentDao.findById(studentId);
        if(!checkIfStudentIsNull(studentId)){
            return null;
        }
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(studentId);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(studentId);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(studentId);

        List<Grade> mathGradeList = new ArrayList<>();
        List<Grade> scienceGradeList = new ArrayList<>();
        List<Grade> historyGradeList = new ArrayList<>();
        mathGrades.forEach(mathGradeList::add);
        scienceGrades.forEach(scienceGradeList::add);
        historyGrades.forEach(historyGradeList::add);

        studentGrades.setHistoryGradeResults(historyGradeList);
        studentGrades.setMathGradeResults(mathGradeList);
        studentGrades.setScienceGradeResults(scienceGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(studentId, student.get().getFirstname(),
                student.get().getLastname(), student.get().getEmailAddress(), studentGrades
        );
        return gradebookCollegeStudent;
    }

    public void configureStudentInformationModel(int studentId, Model m){
        GradebookCollegeStudent gradebookCollegeStudent = getStudentInformation(studentId);
        m.addAttribute("student", gradebookCollegeStudent);
        if(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getMathGradeResults()
            ));
        }else{
            m.addAttribute("mathAverage", "N/A");
        }
        if(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getScienceGradeResults()
            ));
        }else{
            m.addAttribute("scienceAverage", "N/A");
        }
        if(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() > 0){
            m.addAttribute("historyAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults()
            ));
        }else{
            m.addAttribute("historyAverage", "N/A");
        }
    }
}
