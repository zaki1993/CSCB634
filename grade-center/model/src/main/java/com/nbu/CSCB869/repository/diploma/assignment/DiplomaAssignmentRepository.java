package com.nbu.CSCB869.repository.diploma.assignment;

import com.nbu.CSCB869.model.diploma.assignment.DiplomaAssignment;
import com.nbu.CSCB869.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing DiplomaAssignment entities.
 * Provides CRUD operations and custom queries for fetching assignments by various criteria.
 */
@Repository
public interface DiplomaAssignmentRepository extends JpaRepository<DiplomaAssignment, Long> {

    /**
     * Finds all diploma assignments that are approved.
     *
     * @param approved Approval status of the assignment
     * @return List of approved assignments
     */
    List<DiplomaAssignment> findByApproved(boolean approved);

    /**
     * Finds assignments with titles containing the specified keyword.
     *
     * @param title Keyword to search in the title
     * @return List of assignments containing the keyword in their title
     */
    List<DiplomaAssignment> findByTitleContaining(String title);

    /**
     * Finds assignments supervised by a specific teacher.
     *
     * @param supervisor The teacher who is supervising the assignment
     * @return List of assignments supervised by the specified teacher
     */
    List<DiplomaAssignment> findByTeacher(Teacher supervisor);
}
