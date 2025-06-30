package com.nbu.CSCB869.service.diploma.assignment;

import com.nbu.CSCB869.model.diploma.assignment.DiplomaAssignment;
import com.nbu.CSCB869.model.Teacher;
import com.nbu.CSCB869.repository.diploma.assignment.DiplomaAssignmentRepository;
import com.nbu.CSCB869.service.exceptions.DiplomaAssignmentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing DiplomaAssignment entities.
 * Handles business logic and data manipulation for diploma assignments.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DiplomaAssignmentService {

    private final DiplomaAssignmentRepository diplomaAssignmentRepository;

    /**
     * Retrieves all diploma assignments.
     *
     * @return List of all diploma assignments
     */
    public List<DiplomaAssignment> getAllAssignments() {
        return diplomaAssignmentRepository.findAll();
    }

    /**
     * Retrieves a specific diploma assignment by its ID.
     *
     * @param id ID of the assignment
     * @return Optional containing the found assignment or empty if not found
     */
    public DiplomaAssignment getAssignmentById(Long id) {
        return diplomaAssignmentRepository.findById(id).orElseThrow(() -> new DiplomaAssignmentNotFoundException(id));
    }

    /**
     * Saves or updates a diploma assignment.
     *
     * @param assignment The diploma assignment to save or update
     * @return The saved diploma assignment
     */
    public DiplomaAssignment saveAssignment(DiplomaAssignment assignment) {
        return diplomaAssignmentRepository.save(assignment);
    }

    /**
     * Deletes a diploma assignment by its ID.
     *
     * @param id ID of the assignment to delete
     */
    public void deleteAssignment(Long id) {
        DiplomaAssignment toDelete = diplomaAssignmentRepository.findById(id).orElseThrow(() -> new DiplomaAssignmentNotFoundException(id));
        diplomaAssignmentRepository.delete(toDelete);
    }

    /**
     * Retrieves all approved diploma assignments.
     *
     * @return List of approved assignments
     */
    public List<DiplomaAssignment> getApprovedAssignments() {
        return diplomaAssignmentRepository.findByApproved(true);
    }

    /**
     * Retrieves diploma assignments containing a specific keyword in the title.
     *
     * @param title Keyword to search in the title
     * @return List of assignments with titles containing the specified keyword
     */
    public List<DiplomaAssignment> getAssignmentsByTitle(String title) {
        return diplomaAssignmentRepository.findByTitleContaining(title);
    }

    /**
     * Retrieves diploma assignments supervised by a specified teacher.
     *
     * @param supervisor supervisor teacher
     * @return List of assignments supervised by the specified teacher
     */
    public List<DiplomaAssignment> getAssignmentsByTeacher(Teacher supervisor) {
        return diplomaAssignmentRepository.findByTeacher(supervisor);
    }

    public void approveDiplomaAssignment(Long id) {
        // Find the diploma assignment
        DiplomaAssignment assignment = getAssignmentById(id);

        // approve the diploma assignment
        assignment.setApproved(true);

        // save changes
        diplomaAssignmentRepository.save(assignment);
    }
}
