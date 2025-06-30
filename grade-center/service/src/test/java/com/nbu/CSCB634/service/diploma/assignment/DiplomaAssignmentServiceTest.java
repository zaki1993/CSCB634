package com.nbu.CSCB634.service.diploma.assignment;

import com.nbu.CSCB634.model.diploma.assignment.DiplomaAssignment;
import com.nbu.CSCB634.repository.diploma.assignment.DiplomaAssignmentRepository;
import com.nbu.CSCB634.service.exceptions.DiplomaAssignmentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiplomaAssignmentServiceTest {

    @Mock
    private DiplomaAssignmentRepository diplomaAssignmentRepository;

    @InjectMocks
    private DiplomaAssignmentService diplomaAssignmentService;

    @Test
    void getAllAssignments() {
        DiplomaAssignment assignment1 = new DiplomaAssignment();
        DiplomaAssignment assignment2 = new DiplomaAssignment();
        when(diplomaAssignmentRepository.findAll()).thenReturn(Arrays.asList(assignment1, assignment2));

        var assignments = diplomaAssignmentService.getAllAssignments();

        assertEquals(2, assignments.size());
    }

    @Test
    void getAssignmentById_found() {
        DiplomaAssignment assignment = new DiplomaAssignment();
        when(diplomaAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        var result = diplomaAssignmentService.getAssignmentById(1L);

        assertNotNull(result);
    }

    @Test
    void getAssignmentById_notFound() {
        when(diplomaAssignmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DiplomaAssignmentNotFoundException.class, () -> diplomaAssignmentService.getAssignmentById(1L));
    }

    @Test
    void saveAssignment() {
        DiplomaAssignment assignment = new DiplomaAssignment();
        when(diplomaAssignmentRepository.save(assignment)).thenReturn(assignment);

        var result = diplomaAssignmentService.saveAssignment(assignment);

        assertNotNull(result);
    }

    @Test
    void deleteAssignment() {
        DiplomaAssignment assignment = new DiplomaAssignment();
        when(diplomaAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        doNothing().when(diplomaAssignmentRepository).delete(assignment);

        diplomaAssignmentService.deleteAssignment(1L);

        verify(diplomaAssignmentRepository, times(1)).delete(assignment);
    }

    @Test
    void approveDiplomaAssignment() {
        DiplomaAssignment assignment = new DiplomaAssignment();
        when(diplomaAssignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(diplomaAssignmentRepository.save(assignment)).thenReturn(assignment);

        diplomaAssignmentService.approveDiplomaAssignment(1L);

        assertTrue(assignment.isApproved());
    }
}
