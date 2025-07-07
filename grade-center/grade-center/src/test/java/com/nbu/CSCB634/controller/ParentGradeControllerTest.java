package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.GradeService;
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
class ParentGradeControllerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ParentGradeController parentGradeController;

    private MockMvc mockMvc;

    private User parentUser;
    private Student student1;
    private Student student2;
    private Grade grade1;
    private Grade grade2;
    private Subject subject1;
    private Subject subject2;
    private School school;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parentGradeController).build();

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

        subject1 = Subject.builder()
                .id(1L)
                .name("Mathematics")
                .build();

        subject2 = Subject.builder()
                .id(2L)
                .name("Physics")
                .build();

        grade1 = Grade.builder()
                .id(1L)
                .student(student1)
                .subject(subject1)
                .value(5)
                .dateAwarded(LocalDate.now())
                .build();

        grade2 = Grade.builder()
                .id(2L)
                .student(student2)
                .subject(subject2)
                .value(4)
                .dateAwarded(LocalDate.now().minusDays(1))
                .build();
    }

    @Test
    void testListGradesForParent_Success() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.getGradesByParentId(1L)).thenReturn(List.of(grade1, grade2));
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/grades").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/list"))
                .andExpect(model().attributeExists("grades"))
                .andExpect(model().attributeExists("children"))
                .andExpect(model().attributeExists("gradesByStudent"));

        verify(gradeService, times(1)).getGradesByParentId(1L);
        verify(gradeService, times(1)).getStudentsForParent(1L);
    }

    @Test
    void testListGradesForParent_NoGrades() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.getGradesByParentId(1L)).thenReturn(List.of());
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/grades").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/list"))
                .andExpect(model().attributeExists("grades"))
                .andExpect(model().attributeExists("children"));

        verify(gradeService, times(1)).getGradesByParentId(1L);
    }

    @Test
    void testListGradesForChild_Success() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.canParentViewStudent(1L, 1L)).thenReturn(true);
        when(gradeService.getGradesByParentAndStudent(1L, 1L)).thenReturn(List.of(grade1));
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/grades/student/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/by-student"))
                .andExpect(model().attributeExists("grades"))
                .andExpect(model().attributeExists("children"))
                .andExpect(model().attributeExists("currentStudent"))
                .andExpect(model().attributeExists("gradesBySubject"));

        verify(gradeService, times(1)).canParentViewStudent(1L, 1L);
        verify(gradeService, times(1)).getGradesByParentAndStudent(1L, 1L);
    }

    @Test
    void testViewGrade_Success() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade1));
        when(gradeService.canParentViewStudent(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/parent/grades/view/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/view"))
                .andExpect(model().attributeExists("grade"));

        verify(gradeService, times(1)).getGradeById(1L);
        verify(gradeService, times(1)).canParentViewStudent(1L, 1L);
    }

    @Test
    void testListGradesForChild_WithMultipleSubjects() throws Exception {
        Grade mathGrade = Grade.builder()
                .id(3L)
                .student(student1)
                .subject(subject1)
                .value(5)
                .dateAwarded(LocalDate.now())
                .build();

        Grade physicsGrade = Grade.builder()
                .id(4L)
                .student(student1)
                .subject(subject2)
                .value(4)
                .dateAwarded(LocalDate.now().minusDays(1))
                .build();

        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.canParentViewStudent(1L, 1L)).thenReturn(true);
        when(gradeService.getGradesByParentAndStudent(1L, 1L)).thenReturn(List.of(mathGrade, physicsGrade));
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/grades/student/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/by-student"))
                .andExpect(model().attributeExists("gradesBySubject"));

        verify(gradeService, times(1)).getGradesByParentAndStudent(1L, 1L);
    }

    @Test
    void testListGradesForParent_EmptyChildren() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.getGradesByParentId(1L)).thenReturn(List.of());
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of());

        mockMvc.perform(get("/parent/grades").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/list"))
                .andExpect(model().attributeExists("children"));

        verify(gradeService, times(1)).getStudentsForParent(1L);
    }

    @Test
    void testListGradesForParent_WithMultipleStudents() throws Exception {
        Grade student1Grade = Grade.builder()
                .id(3L)
                .student(student1)
                .subject(subject1)
                .value(5)
                .dateAwarded(LocalDate.now())
                .build();

        Grade student2Grade = Grade.builder()
                .id(4L)
                .student(student2)
                .subject(subject2)
                .value(4)
                .dateAwarded(LocalDate.now().minusDays(1))
                .build();

        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.getGradesByParentId(1L)).thenReturn(List.of(student1Grade, student2Grade));
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/grades").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/list"))
                .andExpect(model().attributeExists("gradesByStudent"));

        verify(gradeService, times(1)).getGradesByParentId(1L);
    }

    @Test
    void testListGradesForChild_NoGrades() throws Exception {
        when(authentication.getName()).thenReturn("parent1");
        when(userService.findByUsername("parent1")).thenReturn(Optional.of(parentUser));
        when(gradeService.canParentViewStudent(1L, 1L)).thenReturn(true);
        when(gradeService.getGradesByParentAndStudent(1L, 1L)).thenReturn(List.of());
        when(gradeService.getStudentsForParent(1L)).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/parent/grades/student/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/grades/by-student"))
                .andExpect(model().attributeExists("grades"));

        verify(gradeService, times(1)).getGradesByParentAndStudent(1L, 1L);
    }
} 