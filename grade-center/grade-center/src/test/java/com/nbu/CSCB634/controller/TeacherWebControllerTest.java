package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.SubjectService;
import com.nbu.CSCB634.service.TeacherService;
import com.nbu.CSCB634.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class TeacherWebControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private SchoolService schoolService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherWebController teacherWebController;

    private MockMvc mockMvc;

    private Teacher teacher;
    private User user;
    private School school;
    private Subject subject;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherWebController).build();

        user = User.builder()
                .id(1L)
                .username("teacher1")
                .firstName("John")
                .lastName("Teacher")
                .email("teacher@test.com")
                .build();

        school = School.builder()
                .id(1L)
                .name("Test School")
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
    }

    @Test
    void testListTeachers_Success() throws Exception {
        when(teacherService.getAllTeachers()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/list"))
                .andExpect(model().attributeExists("teachers"));

        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    void testShowCreateForm_Success() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(subjectService.getAllSubjects()).thenReturn(List.of(subject));

        mockMvc.perform(get("/teachers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/form"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("schools"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("isEdit", false));

        verify(schoolService, times(1)).getAllSchools();
        verify(subjectService, times(1)).getAllSubjects();
    }

    @Test
    void testCreateTeacher_Success() throws Exception {
        when(userService.findByUsername("teacher1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("teacher123")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(subjectService.getSubjectById(1L)).thenReturn(Optional.of(subject));
        when(teacherService.createTeacher(any(Teacher.class))).thenReturn(teacher);

        mockMvc.perform(post("/teachers/new")
                .param("username", "teacher1")
                .param("firstName", "John")
                .param("lastName", "Teacher")
                .param("email", "teacher@test.com")
                .param("schoolId", "1")
                .param("qualifiedSubjectIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).createTeacher(any(Teacher.class));
    }

    @Test
    void testCreateTeacher_UsernameExists() throws Exception {
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(subjectService.getAllSubjects()).thenReturn(List.of(subject));

        mockMvc.perform(post("/teachers/new")
                .param("username", "teacher1")
                .param("firstName", "John")
                .param("lastName", "Teacher")
                .param("email", "teacher@test.com")
                .param("schoolId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/form"));

        verify(userService, never()).save(any(User.class));
        verify(teacherService, never()).createTeacher(any(Teacher.class));
    }

    @Test
    void testCreateTeacher_WithException() throws Exception {
        when(userService.findByUsername("teacher1")).thenReturn(Optional.empty());
        when(userService.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/teachers/new")
                .param("username", "teacher1")
                .param("firstName", "John")
                .param("lastName", "Teacher")
                .param("email", "teacher@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers/new"));

        verify(userService, times(1)).save(any(User.class));
        verify(teacherService, never()).createTeacher(any(Teacher.class));
    }

    @Test
    void testShowEditForm_Success() throws Exception {
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(subjectService.getAllSubjects()).thenReturn(List.of(subject));

        mockMvc.perform(get("/teachers/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/form"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attribute("isEdit", true));

        verify(teacherService, times(1)).getTeacherById(1L);
    }

    @Test
    void testShowEditForm_NotFound() throws Exception {
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teachers/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).getTeacherById(99L);
    }

    @Test
    void testUpdateTeacher_Success() throws Exception {
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));
        when(userService.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(subjectService.getSubjectById(1L)).thenReturn(Optional.of(subject));
        when(teacherService.updateTeacher(anyLong(), any(Teacher.class))).thenReturn(teacher);

        mockMvc.perform(post("/teachers/1/edit")
                .param("username", "teacher1")
                .param("firstName", "John Updated")
                .param("lastName", "Teacher Updated")
                .param("email", "updated@test.com")
                .param("schoolId", "1")
                .param("qualifiedSubjectIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).updateTeacher(anyLong(), any(Teacher.class));
    }

    @Test
    void testUpdateTeacher_NotFound() throws Exception {
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/teachers/99/edit")
                .param("username", "teacher1")
                .param("firstName", "John")
                .param("lastName", "Teacher")
                .param("email", "teacher@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).getTeacherById(99L);
        verify(teacherService, never()).updateTeacher(anyLong(), any(Teacher.class));
    }

    @Test
    void testDeleteTeacher_Success() throws Exception {
        doNothing().when(teacherService).deleteTeacher(1L);

        mockMvc.perform(post("/teachers/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).deleteTeacher(1L);
    }

    @Test
    void testDeleteTeacher_WithException() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(teacherService).deleteTeacher(1L);

        mockMvc.perform(post("/teachers/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).deleteTeacher(1L);
    }

    @Test
    void testViewTeacher_Success() throws Exception {
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));

        mockMvc.perform(get("/teachers/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/view"))
                .andExpect(model().attributeExists("teacher"));

        verify(teacherService, times(1)).getTeacherById(1L);
    }

    @Test
    void testViewTeacher_NotFound() throws Exception {
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teachers/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).getTeacherById(99L);
    }

    @Test
    void testCreateTeacher_WithoutQualifiedSubjects() throws Exception {
        when(userService.findByUsername("teacher1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("teacher123")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(teacherService.createTeacher(any(Teacher.class))).thenReturn(teacher);

        mockMvc.perform(post("/teachers/new")
                .param("username", "teacher1")
                .param("firstName", "John")
                .param("lastName", "Teacher")
                .param("email", "teacher@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).createTeacher(any(Teacher.class));
    }
} 