package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.ParentDto;
import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.ParentService;
import com.nbu.CSCB634.service.StudentService;
import com.nbu.CSCB634.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParentWebControllerTest {

    @Mock
    private ParentService parentService;

    @Mock
    private UserService userService;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private ParentWebController parentWebController;

    private MockMvc mockMvc;

    private Parent parent;
    private User user;
    private Student student;
    private ParentDto parentDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parentWebController).build();

        user = User.builder()
                .id(1L)
                .username("parent1")
                .firstName("John")
                .lastName("Doe")
                .email("parent@test.com")
                .role(Role.PARENT)
                .build();

        User user = User.builder()
                .id(1L)
                .username("student1")
                .firstName("Child1")
                .lastName("Student")
                .email("teacher@test.com")
                .role(Role.STUDENT)
                .build();
        student = Student.builder()
                .id(1L)
                .user(user)
                .build();

        parent = Parent.builder()
                .id(1L)
                .user(user)
                .students(Set.of(student))
                .build();

        parentDto = ParentDto.builder()
                .id(1L)
                .username("parent1")
                .firstName("John")
                .lastName("Doe")
                .email("parent@test.com")
                .studentIds(Set.of(1L))
                .build();
    }

    @Test
    void testListParents_Success() throws Exception {
        when(parentService.getAllParents()).thenReturn(List.of(parent));

        mockMvc.perform(get("/parents"))
                .andExpect(status().isOk())
                .andExpect(view().name("parents/list"))
                .andExpect(model().attributeExists("parents"));

        verify(parentService, times(1)).getAllParents();
    }

    @Test
    void testListParents_EmptyList() throws Exception {
        when(parentService.getAllParents()).thenReturn(List.of());

        mockMvc.perform(get("/parents"))
                .andExpect(status().isOk())
                .andExpect(view().name("parents/list"))
                .andExpect(model().attributeExists("parents"));

        verify(parentService, times(1)).getAllParents();
    }

    @Test
    void testCreateParentForm_Success() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/parents/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("parents/form"))
                .andExpect(model().attributeExists("parent"))
                .andExpect(model().attributeExists("students"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testCreateParent_Success() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(parentService.createParent(any(Parent.class))).thenReturn(parent);
        when(studentService.getStudentById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com")
                .param("studentIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(userService, times(1)).registerUser(any(User.class));
        verify(parentService, times(1)).createParent(any(Parent.class));
    }

    @Test
    void testCreateParent_UserRegistrationFallback() throws Exception {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("User exists"))
                .thenReturn(user);
        when(userService.getNextAvailableId()).thenReturn(2L);
        when(parentService.createParent(any(Parent.class))).thenReturn(parent);

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(userService, times(2)).registerUser(any(User.class));
        verify(userService, times(1)).getNextAvailableId();
    }

    @Test
    void testCreateParent_ParentCreationFallback() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(parentService.createParent(any(Parent.class)))
                .thenThrow(new RuntimeException("Parent exists"))
                .thenReturn(parent);
        when(parentService.getNextAvailableId()).thenReturn(2L);

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(parentService, times(2)).createParent(any(Parent.class));
        verify(parentService, times(1)).getNextAvailableId();
    }

    @Test
    void testCreateParent_WithoutStudents() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(parentService.createParent(any(Parent.class))).thenReturn(parent);

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(userService, times(1)).registerUser(any(User.class));
        verify(parentService, times(1)).createParent(any(Parent.class));
    }

    @Test
    void testCreateParent_WithException() throws Exception {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("Fatal error"));
        when(userService.getNextAvailableId()).thenReturn(2L);
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("parents/form"))
                .andExpect(model().attributeExists("students"));

        verify(userService, times(2)).registerUser(any(User.class));
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testViewParent_Success() throws Exception {
        when(parentService.getParentById(1L)).thenReturn(Optional.of(parent));

        mockMvc.perform(get("/parents/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("parents/view"))
                .andExpect(model().attributeExists("parent"));

        verify(parentService, times(1)).getParentById(1L);
    }

    @Test
    void testEditParentForm_Success() throws Exception {
        when(parentService.getParentById(1L)).thenReturn(Optional.of(parent));
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/parents/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("parents/form"))
                .andExpect(model().attributeExists("parent"))
                .andExpect(model().attributeExists("students"));

        verify(parentService, times(1)).getParentById(1L);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testDeleteParent_Success() throws Exception {
        doNothing().when(parentService).deleteParent(1L);

        mockMvc.perform(post("/parents/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(parentService, times(1)).deleteParent(1L);
    }

    @Test
    void testDeleteParent_WithException() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(parentService).deleteParent(1L);

        mockMvc.perform(post("/parents/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(parentService, times(1)).deleteParent(1L);
    }

    @Test
    void testCreateParent_WithMultipleStudents() throws Exception {
        Student student2 = Student.builder().id(2L).build();
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(parentService.createParent(any(Parent.class))).thenReturn(parent);
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));
        when(studentService.getStudentById(2L)).thenReturn(Optional.of(student2));

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com")
                .param("studentIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(studentService, times(1)).getStudentById(1L);
        verify(studentService, times(1)).getStudentById(2L);
    }

    @Test
    void testCreateParent_StudentNotFound() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(parentService.createParent(any(Parent.class))).thenReturn(parent);
        when(studentService.getStudentById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/parents/create")
                .param("username", "parent1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "parent@test.com")
                .param("studentIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parents"));

        verify(studentService, times(1)).getStudentById(1L);
    }
} 