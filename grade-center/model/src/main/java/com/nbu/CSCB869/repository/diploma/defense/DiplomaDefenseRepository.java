package com.nbu.CSCB869.repository.diploma.defense;

import com.nbu.CSCB869.model.Student;
import com.nbu.CSCB869.model.diploma.defense.DiplomaDefense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing DiplomaDefense entities.
 * Provides CRUD operations and custom queries for fetching defenses by date and other criteria.
 */
@Repository
public interface DiplomaDefenseRepository extends JpaRepository<DiplomaDefense, Long> {
    @Query("SELECT AVG(SIZE(d.students)) FROM DiplomaDefense d WHERE d.defenseDate BETWEEN :startDate AND :endDate")
    Double findAverageStudentsPerDefenseInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM diplomathesis t " +
            "JOIN diplomaassignment da ON da.id = t.assignment_id " +
            "JOIN teacher te ON te.id = da.teacher_id " +
            "JOIN defense_grades dd ON dd.thesis_id = t.id " +
            "WHERE te.id = :teacherId AND dd.grade >= 3.0", nativeQuery = true)
    long countSuccessfulDefensesByTeacher(@Param("teacherId") Long teacherId);

    @Query(value = "SELECT MAX(d.grade) FROM defense_grades d WHERE d.thesis_id = :thesisId", nativeQuery = true)
    Double findGradeByThesisId(@Param("thesisId") Long thesisId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM defense_grades d WHERE d.thesis_id = :thesisId)", nativeQuery = true)
    boolean gradeExistForThesis(@Param("thesisId") Long thesisId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM defense_grades d WHERE d.thesis_id = :thesisId AND d.grade >= 3.0)", nativeQuery = true)
    boolean isGradePositive(@Param("thesisId") Long thesisId);


    @Query(value = "SELECT NOT EXISTS (SELECT 1 FROM defense_grades d WHERE d.defense_id = (SELECT defense_id from thesisreview WHERE thesis_id = :thesisId) AND d.thesis_id = :thesisId)", nativeQuery = true)
    boolean isNotGraded(@Param("thesisId") Long thesisId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM defense_thesis dt WHERE dt.thesis_id = :thesisId)", nativeQuery = true)
    boolean isThesisInDefense(@Param("thesisId") Long thesisId);
}
