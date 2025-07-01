package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchoolClassId(Long schoolClassId);
    List<Student> findBySchoolId(Long schoolId);
}