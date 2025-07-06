package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findBySchoolId(Long schoolId);

    List<Teacher> findByQualifiedSubjectsContaining(Subject subject);
}