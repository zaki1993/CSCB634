package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Student entities.
 * Provides CRUD operations and allows custom queries.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAll();

    Optional<Student> findByFn(String fn);

    @Query("SELECT s FROM Student s " +
            "JOIN DiplomaAssignment da ON da.student.id = s.id " +
            "JOIN DiplomaThesis dt ON dt.assignment.id = da.id " +
            "JOIN ThesisReview tr ON tr.thesis.id = dt.id " +
            "JOIN DiplomaDefense dd ON dd.id = tr.defense.id " +
            "WHERE dd.defenseDate BETWEEN :startDate AND :endDate")
    List<Student> findGraduatedStudentsInPeriod(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}
