package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchoolId(Long schoolId);
    List<Student> findBySchoolClass(SchoolClass schoolClass);
    
    // Find students that a specific teacher can manage
    // (students from the same school as the teacher)
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId")
    List<Student> findStudentsBySchoolId(@Param("schoolId") Long schoolId);
    
    // Find students by school class ID
    @Query("SELECT s FROM Student s WHERE s.schoolClass.id = :classId")
    List<Student> findBySchoolClassId(@Param("classId") Long classId);
}