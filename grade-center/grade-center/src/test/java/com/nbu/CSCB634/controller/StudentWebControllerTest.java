package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.StudentDto;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.SchoolClassService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.StudentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StudentWebControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private SchoolService schoolService;

    @Mock
    private SchoolClassService schoolClassService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentWebController studentWebController;

    private MockMvc mockMvc;

    private Student student;
    private User user;
    private School school;
    private SchoolClass schoolClass;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentWebController).build();

        user = User.builder()
                .id(1L)
                .username("student1")
                .firstName("John")
                .lastName("Student")
                .email("student@test.com")
                .build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .name("Class 10A")
                .school(school)
                .build();

        student = Student.builder()
                .id(1L)
                .user(user)
                .school(school)
                .schoolClass(schoolClass)
                .build();
    }

    @Test
    void testListStudents_Success() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testListStudents_EmptyList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testShowCreateForm_Success() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(get("/students/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("schools"))
                .andExpect(model().attribute("isEdit", false));

        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testCreateStudent_Success() throws Exception {
        when(userService.findByUsername("student1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("student123")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(schoolClassService.getSchoolClassById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/students/new")
                .param("username", "student1")
                .param("firstName", "John")
                .param("lastName", "Student")
                .param("email", "student@test.com")
                .param("schoolId", "1")
                .param("schoolClassId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).createStudent(any(Student.class));
    }

   /* @Test
    void testCreateStudent_UsernameExists() throws Exception {
        when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(schoolClassService.getSchoolClassesBySchoolId(1L)).thenReturn(List.of(schoolClass));

        mockMvc.perform(post("/students/new")
                .param("username", "student1")
                .param("firstName", "John")
                .param("lastName", "Student")
                .param("email", "student@test.com")
                .param("schoolId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("schools"));

        verify(userService, never()).save(any(User.class));
        verify(studentService, never()).createStudent(any(Student.class));
    }*/

    @Test
    void testCreateStudent_WithoutSchoolClass() throws Exception {
        when(userService.findByUsername("student1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("student123")).thenReturn("encoded");
        when(userService.save(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/students/new")
                .param("username", "student1")
                .param("firstName", "John")
                .param("lastName", "Student")
                .param("email", "student@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).createStudent(any(Student.class));
    }

    @Test
    void testCreateStudent_WithException() throws Exception {
        when(userService.findByUsername("student1")).thenReturn(Optional.empty());
        when(userService.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/students/new")
                .param("username", "student1")
                .param("firstName", "John")
                .param("lastName", "Student")
                .param("email", "student@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students/new"));

        verify(userService, times(1)).save(any(User.class));
        verify(studentService, never()).createStudent(any(Student.class));
    }

/*    @Test
    void testShowEditForm_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(schoolClassService.getSchoolClassesBySchoolId(1L)).thenReturn(List.of(schoolClass));

        mockMvc.perform(get("/students/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("isEdit", true));

        verify(studentService, times(1)).getStudentById(1L);
        verify(schoolService, times(1)).getAllSchools();
    }*/

    @Test
    void testShowEditForm_NotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).getStudentById(99L);
    }

    @Test
    void testUpdateStudent_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));
        when(userService.findByUsername("student1")).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(schoolClassService.getSchoolClassById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentService.updateStudent(anyLong(), any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/students/1/edit")
                .param("username", "student1")
                .param("firstName", "John Updated")
                .param("lastName", "Student Updated")
                .param("email", "updated@test.com")
                .param("schoolId", "1")
                .param("schoolClassId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).updateStudent(anyLong(), any(Student.class));
    }

    @Test
    void testUpdateStudent_NotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/students/99/edit")
                .param("username", "student1")
                .param("firstName", "John")
                .param("lastName", "Student")
                .param("email", "student@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).getStudentById(99L);
        verify(studentService, never()).updateStudent(anyLong(), any(Student.class));
    }

    @Test
    void testViewStudent_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/view"))
                .andExpect(model().attributeExists("student"));

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    void testViewStudent_NotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).getStudentById(99L);
    }
/*
    @Test
    void testGetSchoolClassesApi_Success() throws Exception {
        when(schoolClassService.getSchoolClassesBySchoolId(1L)).thenReturn(List.of(schoolClass));

        mockMvc.perform(get("/students/api/school-classes")
                .param("schoolId", "1"))
                .andExpect(status().isOk());

        verify(schoolClassService, times(1)).getSchoolClassesBySchoolId(1L);
    }*/
} 