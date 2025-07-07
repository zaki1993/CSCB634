package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Absence;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AbsenceWebControllerTest {

    @Mock
    private AbsenceService absenceService;

    @Mock
    private StudentService studentService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AbsenceWebController absenceWebController;

    private MockMvc mockMvc;

    private User user;
    private Teacher teacher;
    private Student student;
    private Absence absence;
    private School school;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(absenceWebController).build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        user = User.builder()
                .id(1L)
                .username("teacher1")
                .firstName("John")
                .lastName("Doe")
                .email("teacher@test.com")
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .user(user)
                .school(school)
                .build();

        User user = User.builder()
                .id(1L)
                .username("johndoe")
                .password("password")
                .build();
        student = Student.builder()
                .id(1L)
                .user(user)
                .school(school)
                .build();

        absence = Absence.builder()
                .id(1L)
                .student(student)
                .teacher(teacher)
                .absenceDate(LocalDate.now())
                .justified(false)
                .build();
    }
    @Test
    void testCreateAbsenceForm_Success() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(absenceService.getStudentsForTeacher(1L)).thenReturn(List.of(student));
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));

        mockMvc.perform(get("/absences/create").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("absences/form"))
                .andExpect(model().attributeExists("absenceDto"))
                .andExpect(model().attributeExists("students"));

        verify(absenceService, times(1)).getStudentsForTeacher(1L);
        verify(teacherService, times(1)).getTeacherById(1L);
    }

    @Test
    void testCreateAbsence_UnauthorizedStudent() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(absenceService.canTeacherManageStudent(anyLong(), anyLong())).thenReturn(false);

        mockMvc.perform(post("/absences/create")
                .param("studentId", "1")
                .param("absenceDate", "2024-01-01")
                .param("justified", "false")
                .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/absences/create"));

        verify(absenceService, never()).createAbsence(any(Absence.class));
    }

    @Test
    void testEditAbsenceForm_Success() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(absenceService.getAbsenceById(1L)).thenReturn(Optional.of(absence));
        when(absenceService.getStudentsForTeacher(1L)).thenReturn(List.of(student));
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));

        mockMvc.perform(get("/absences/edit/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("absences/form"))
                .andExpect(model().attributeExists("absenceDto"))
                .andExpect(model().attributeExists("students"));

        verify(absenceService, times(1)).getAbsenceById(1L);
    }

    @Test
    void testDeleteAbsence_Success() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(absenceService.getAbsenceById(1L)).thenReturn(Optional.of(absence));
        doNothing().when(absenceService).deleteAbsence(1L);

        mockMvc.perform(post("/absences/delete/1").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/absences"));

        verify(absenceService, times(1)).deleteAbsence(1L);
    }
} 