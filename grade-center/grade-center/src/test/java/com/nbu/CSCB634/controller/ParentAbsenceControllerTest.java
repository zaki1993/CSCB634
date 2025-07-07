package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Absence;
import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.AbsenceService;
import com.nbu.CSCB634.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParentAbsenceControllerTest {

    @Mock
    private AbsenceService absenceService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ParentAbsenceController parentAbsenceController;

    private MockMvc mockMvc;

    private User parentUser;
    private Student student1;
    private Student student2;
    private Absence absence1;
    private Absence absence2;
    private School school;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parentAbsenceController).build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        parentUser = User.builder()
                .id(1L)
                .username("parent1")
                .firstName("John")
                .lastName("Parent")
                .email("parent@test.com")
                .build();

        User student = User.builder()
                .id(1L)
                .username("student1")
                .firstName("Child1")
                .lastName("Student")
                .email("teacher@test.com")
                .role(Role.STUDENT)
                .build();
        User user1 = User.builder()
                .id(1L)
                .username("student2")
                .firstName("Child2")
                .lastName("Student")
                .email("teacher@test.com")
                .role(Role.STUDENT)
                .build();
        student1 = Student.builder()
                .id(1L)
                .user(student)
                .school(school)
                .build();

        student2 = Student.builder()
                .id(2L)
                .user(user1)
                .school(school)
                .build();

        absence1 = Absence.builder()
                .id(1L)
                .student(student1)
                .absenceDate(LocalDate.now())
                .justified(true)
                .build();

        absence2 = Absence.builder()
                .id(2L)
                .student(student2)
                .absenceDate(LocalDate.now().minusDays(1))
                .justified(false)
                .build();
    }

    @Test
    void testListAbsencesForParent_Success() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.getAbsencesByParentId(1L)).thenReturn(List.of(absence1, absence2));
        when(absenceService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/absences").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/list"))
                .andExpect(model().attributeExists("absences"))
                .andExpect(model().attributeExists("children"))
                .andExpect(model().attributeExists("absencesByStudent"))
                .andExpect(model().attributeExists("justifiedCount"))
                .andExpect(model().attributeExists("unjustifiedCount"));

        verify(absenceService, times(1)).getAbsencesByParentId(1L);
        verify(absenceService, times(1)).getStudentsForParent(1L);
    }

    @Test
    void testListAbsencesForParent_NoAbsences() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.getAbsencesByParentId(1L)).thenReturn(List.of());
        when(absenceService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/absences").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/list"))
                .andExpect(model().attributeExists("absences"))
                .andExpect(model().attributeExists("children"));

        verify(absenceService, times(1)).getAbsencesByParentId(1L);
    }

    @Test
    void testListAbsencesForChild_Success() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.canParentViewStudent(1L, 1L)).thenReturn(true);
        when(absenceService.getAbsencesByParentAndStudent(1L, 1L)).thenReturn(List.of(absence1));
        when(absenceService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/absences/student/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/by-student"))
                .andExpect(model().attributeExists("absences"))
                .andExpect(model().attributeExists("children"))
                .andExpect(model().attributeExists("currentStudent"))
                .andExpect(model().attributeExists("justifiedCount"))
                .andExpect(model().attributeExists("unjustifiedCount"))
                .andExpect(model().attributeExists("pendingCount"));

        verify(absenceService, times(1)).canParentViewStudent(1L, 1L);
        verify(absenceService, times(1)).getAbsencesByParentAndStudent(1L, 1L);
    }

    @Test
    void testViewAbsence_Success() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.getAbsenceById(1L)).thenReturn(Optional.of(absence1));
        when(absenceService.canParentViewStudent(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/parent/absences/view/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/view"))
                .andExpect(model().attributeExists("absence"));

        verify(absenceService, times(1)).getAbsenceById(1L);
        verify(absenceService, times(1)).canParentViewStudent(1L, 1L);
    }

    @Test
    void testListAbsencesForChild_WithPendingAbsences() throws Exception {
        Absence pendingAbsence = Absence.builder()
                .id(3L)
                .student(student1)
                .absenceDate(LocalDate.now().minusDays(2))
                .justified(null) // Pending
                .build();

        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.canParentViewStudent(1L, 1L)).thenReturn(true);
        when(absenceService.getAbsencesByParentAndStudent(1L, 1L)).thenReturn(List.of(absence1, pendingAbsence));
        when(absenceService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/absences/student/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/by-student"))
                .andExpect(model().attributeExists("pendingCount"));

        verify(absenceService, times(1)).getAbsencesByParentAndStudent(1L, 1L);
    }

    @Test
    void testListAbsencesForParent_EmptyChildren() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.getAbsencesByParentId(1L)).thenReturn(List.of());
        when(absenceService.getStudentsForParent(1L)).thenReturn(List.of());

        mockMvc.perform(get("/parent/absences").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/list"))
                .andExpect(model().attributeExists("children"));

        verify(absenceService, times(1)).getStudentsForParent(1L);
    }

    @Test
    void testListAbsencesForParent_MixedJustificationStatus() throws Exception {
        Absence justified = Absence.builder()
                .id(3L)
                .student(student1)
                .absenceDate(LocalDate.now())
                .justified(true)
                .build();

        Absence unjustified = Absence.builder()
                .id(4L)
                .student(student1)
                .absenceDate(LocalDate.now().minusDays(1))
                .justified(false)
                .build();

        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(absenceService.getAbsencesByParentId(1L)).thenReturn(List.of(justified, unjustified));
        when(absenceService.getStudentsForParent(1L)).thenReturn(List.of(student1));

        mockMvc.perform(get("/parent/absences").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/absences/list"))
                .andExpect(model().attributeExists("justifiedCount"))
                .andExpect(model().attributeExists("unjustifiedCount"));

        verify(absenceService, times(1)).getAbsencesByParentId(1L);
    }
} 