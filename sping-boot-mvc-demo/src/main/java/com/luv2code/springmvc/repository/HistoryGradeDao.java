package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradeDao extends CrudRepository<HistoryGrade, Integer> {
    Iterable<HistoryGrade> findGradeByStudentId(int id);
    void deleteByStudentId(int id);
}
