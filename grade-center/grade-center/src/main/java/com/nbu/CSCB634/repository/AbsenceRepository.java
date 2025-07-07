package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Absence;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByStudent(Student student);
    List<Absence> findByTeacher(Teacher teacher);
    List<Absence> findByStudentAndAbsenceDate(Student student, LocalDate absenceDate);
    List<Absence> findByStudentAndAbsenceDateBetween(Student student, LocalDate startDate, LocalDate endDate);
} 