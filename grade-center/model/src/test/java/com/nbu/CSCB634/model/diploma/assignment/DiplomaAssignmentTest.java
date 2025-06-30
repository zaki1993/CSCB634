package com.nbu.CSCB634.model.diploma.assignment;

import com.nbu.CSCB634.model.diploma.thesis.DiplomaThesis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiplomaAssignmentTest {

    private DiplomaAssignment assignment;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        assignment = new DiplomaAssignment();
        teacher = new Teacher();
        teacher.setUsername("teacher1");
        assignment.setTeacher(teacher);
    }

    @Test
    void testIsReadOnly_NoTeacher() {
        assignment.setTeacher(null);

        assertTrue(assignment.isReadOnly(), "Assignment should be read-only if there is no teacher.");
    }

    @Test
    void testIsReadOnly_DifferentUser() {
        try (MockedStatic<SecurityContextHolder> mockedContext = Mockito.mockStatic(SecurityContextHolder.class)) {
            // Mock SecurityContext to simulate a logged-in user
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn("anotherTeacher");
            when(securityContext.getAuthentication()).thenReturn(authentication);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertTrue(assignment.isReadOnly(), "Assignment should be read-only if the logged-in user is not the teacher.");
        }
    }

    @Test
    void testIsReadOnly_ApprovedAssignment() {
        try (MockedStatic<SecurityContextHolder> mockedContext = Mockito.mockStatic(SecurityContextHolder.class)) {
            // Mock SecurityContext to simulate a logged-in teacher
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn("teacher1");
            when(securityContext.getAuthentication()).thenReturn(authentication);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assignment.setApproved(true);

            assertTrue(assignment.isReadOnly(), "Assignment should be read-only if it is approved.");
        }
    }

    @Test
    void testIsReadOnly_EditableAssignment() {
        try (MockedStatic<SecurityContextHolder> mockedContext = Mockito.mockStatic(SecurityContextHolder.class)) {
            // Mock SecurityContext to simulate a logged-in teacher
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn("teacher1");
            when(securityContext.getAuthentication()).thenReturn(authentication);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assignment.setApproved(false);

            assertFalse(assignment.isReadOnly(), "Assignment should be editable if the logged-in user is the teacher and it is not approved.");
        }
    }

    @Test
    void testHasDiplomaThesis_NoThesis() {
        assertFalse(assignment.hasDiplomaThesis(), "Assignment should not have a diploma thesis if the thesis is null.");
    }

    @Test
    void testHasDiplomaThesis_WithThesis() {
        DiplomaThesis thesis = new DiplomaThesis();
        assignment.setThesis(thesis);

        assertTrue(assignment.hasDiplomaThesis(), "Assignment should have a diploma thesis if the thesis is set.");
    }
}
