package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.global.AccessControlConfig;
import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.*;
import com.nbu.CSCB634.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GradeWebControllerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private StudentService studentService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private AcademicTermService academicTermService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GradeWebController gradeWebController;

    private MockMvc mockMvc;

    private Grade grade;
    private User user;
    private Teacher teacher;
    private Student student;
    private Subject subject;
    private AcademicTerm academicTerm;
    private School school;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gradeWebController).build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        user = User.builder()
                .id(1L)
                .username("teacher1")
                .firstName("John")
                .lastName("Teacher")
                .email("teacher@test.com")
                .role(Role.TEACHER)
                .build();

        subject = Subject.builder()
                .id(1L)
                .name("Mathematics")
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .user(user)
                .school(school)
                .qualifiedSubjects(Set.of(subject))
                .build();

        User user1 = User.builder()
                .id(1L)
                .username("student1")
                .firstName("John")
                .lastName("student1")
                .email("student1@test.com")
                .role(Role.TEACHER)
                .build();
        student = Student.builder()
                .id(1L)
                .user(user1)
                .school(school)
                .build();

        academicTerm = AcademicTerm.builder()
                .id(1L)
                .name("Term 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .school(school)
                .build();

        grade = Grade.builder()
                .id(1L)
                .student(student)
                .teacher(teacher)
                .subject(subject)
                .value(5)
                .dateAwarded(LocalDate.now())
                .build();
    }

    @Test
    void testListGrades_AsTeacher() throws Exception {
        try (MockedStatic<AccessControlConfig> mockedStatic = mockStatic(AccessControlConfig.class)) {
            mockedStatic.when(AccessControlConfig::isTeacher).thenReturn(true);
            
            when(authentication.getName()).thenReturn("teacher1");
            when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
            when(gradeService.getGradesByTeacherId(1L)).thenReturn(List.of(grade));

            mockMvc.perform(get("/grades").principal(authentication))
                    .andExpect(status().isOk())
                    .andExpect(view().name("grades/list"))
                    .andExpect(model().attributeExists("grades"));

            verify(gradeService, times(1)).getGradesByTeacherId(1L);
        }
    }

    @Test
    void testListGrades_AsAdmin() throws Exception {
        try (MockedStatic<AccessControlConfig> mockedStatic = mockStatic(AccessControlConfig.class)) {
            mockedStatic.when(AccessControlConfig::isTeacher).thenReturn(false);
            
            when(gradeService.getAll()).thenReturn(List.of(grade));

            mockMvc.perform(get("/grades").principal(authentication))
                    .andExpect(status().isOk())
                    .andExpect(view().name("grades/list"))
                    .andExpect(model().attributeExists("grades"));

            verify(gradeService, times(1)).getAll();
        }
    }

    @Test
    void testViewGrade_AsTeacher_OwnGrade() throws Exception {
        try (MockedStatic<AccessControlConfig> mockedStatic = mockStatic(AccessControlConfig.class)) {
            mockedStatic.when(AccessControlConfig::isTeacher).thenReturn(true);
            
            when(authentication.getName()).thenReturn("teacher1");
            when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
            when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));

            mockMvc.perform(get("/grades/view/1").principal(authentication))
                    .andExpect(status().isOk())
                    .andExpect(view().name("grades/view"))
                    .andExpect(model().attributeExists("grade"));

            verify(gradeService, times(1)).getGradeById(1L);
        }
    }
    @Test
    void testViewGrade_AsAdmin() throws Exception {
        try (MockedStatic<AccessControlConfig> mockedStatic = mockStatic(AccessControlConfig.class)) {
            mockedStatic.when(AccessControlConfig::isTeacher).thenReturn(false);
            
            when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));

            mockMvc.perform(get("/grades/view/1").principal(authentication))
                    .andExpect(status().isOk())
                    .andExpect(view().name("grades/view"))
                    .andExpect(model().attributeExists("grade"));

            verify(gradeService, times(1)).getGradeById(1L);
        }
    }

    @Test
    void testCreateGradeForm_Success() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.getStudentsForTeacher(1L)).thenReturn(List.of(student));
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));
        when(academicTermService.getAllAcademicTerms()).thenReturn(List.of(academicTerm));

        mockMvc.perform(get("/grades/create").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("grades/form"))
                .andExpect(model().attributeExists("gradeDto"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("academicTerms"));

        verify(gradeService, times(1)).getStudentsForTeacher(1L);
        verify(teacherService, times(1)).getTeacherById(1L);
        verify(academicTermService, times(1)).getAllAcademicTerms();
    }

    @Test
    void testCreateGrade_CannotManageStudent() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.canTeacherManageStudent(1L, 1L)).thenReturn(false);

        mockMvc.perform(post("/grades/create")
                .principal(authentication)
                .param("studentId", "1")
                .param("subjectId", "1")
                .param("value", "5")
                .param("academicTermId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/grades/create"));

        verify(gradeService, times(1)).canTeacherManageStudent(1L, 1L);
        verify(gradeService, never()).createGrade(any(Grade.class));
    }

    @Test
    void testEditGradeForm_Success() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));
        when(gradeService.getStudentsForTeacher(1L)).thenReturn(List.of(student));
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));
        when(academicTermService.getAllAcademicTerms()).thenReturn(List.of(academicTerm));

        mockMvc.perform(get("/grades/edit/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("grades/form"))
                .andExpect(model().attributeExists("gradeDto"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("academicTerms"));

        verify(gradeService, times(1)).getGradeById(1L);
    }

    @Test
    void testUpdateGrade_NotOwnGrade() throws Exception {
        Teacher otherTeacher = Teacher.builder()
                .id(2L)
                .user(User.builder().id(2L).username("teacher2").build())
                .build();
        
        Grade otherGrade = Grade.builder()
                .id(2L)
                .teacher(otherTeacher)
                .build();
        
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.getGradeById(2L)).thenReturn(Optional.of(otherGrade));

        mockMvc.perform(post("/grades/edit/2")
                .principal(authentication)
                .param("studentId", "1")
                .param("subjectId", "1")
                .param("value", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/grades"));

        verify(gradeService, times(1)).getGradeById(2L);
        verify(gradeService, never()).updateGrade(anyLong(), any(Grade.class));
    }

    @Test
    void testDeleteGrade_Success() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));
        doNothing().when(gradeService).deleteGrade(1L);

        mockMvc.perform(post("/grades/delete/1").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/grades"));

        verify(gradeService, times(1)).deleteGrade(1L);
    }

    @Test
    void testDeleteGrade_NotOwnGrade() throws Exception {
        Teacher otherTeacher = Teacher.builder()
                .id(2L)
                .user(User.builder().id(2L).username("teacher2").build())
                .build();
        
        Grade otherGrade = Grade.builder()
                .id(2L)
                .teacher(otherTeacher)
                .build();
        
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.getGradeById(2L)).thenReturn(Optional.of(otherGrade));

        mockMvc.perform(post("/grades/delete/2").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/grades"));

        verify(gradeService, times(1)).getGradeById(2L);
        verify(gradeService, never()).deleteGrade(anyLong());
    }

    @Test
    void testDeleteGrade_WithException() throws Exception {
        when(authentication.getName()).thenReturn("teacher1");
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));
        doThrow(new RuntimeException("Delete failed")).when(gradeService).deleteGrade(1L);

        mockMvc.perform(post("/grades/delete/1").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/grades"));

        verify(gradeService, times(1)).deleteGrade(1L);
    }

} 