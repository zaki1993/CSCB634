package com.nbu.CSCB634.repository.diploma.thesis;

import com.nbu.CSCB634.model.diploma.thesis.DiplomaThesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiplomaThesisRepository extends JpaRepository<DiplomaThesis, Long> {

    @Query(value = "SELECT t.* " +
            "FROM diplomathesis t " +
            "WHERE t.id IN (SELECT thesis_id as id from defense_grades dg where dg.grade BETWEEN :minGrade AND :maxGrade)", nativeQuery = true)
    List<DiplomaThesis> findThesesWithGradesInRange(@Param("minGrade") double minGrade, @Param("maxGrade") double maxGrade);
}
