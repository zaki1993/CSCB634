package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(Student student);
    List<Grade> findBySubject(Subject subject);
    List<Grade> findByTeacher(Teacher teacher);
}