package com.nbu.CSCB634.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Student student;

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @BeforeEach
    void setup() {
        student = Student.builder()
                .id(1L)
                .user(user)
                .build();
    }

    @Test
    void testGetStudentById_Found() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testGetStudentById_NotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllStudents() throws Exception {
        Student s2 = Student.builder().id(2L).user(user).build();

        when(studentService.getAllStudents()).thenReturn(List.of(student, s2));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCreateStudent() throws Exception {
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testUpdateStudent_Found() throws Exception {
        Student updated = Student.builder()
                .id(1L)
                .user(user)
                .build();

        when(studentService.updateStudent(eq(1L), any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice Updated"));
    }

    @Test
    void testUpdateStudent_NotFound() throws Exception {
        when(studentService.updateStudent(eq(99L), any(Student.class)))
                .thenThrow(new IllegalArgumentException("Student not found"));

        mockMvc.perform(put("/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student not found"));
    }

    @Test
    void testDeleteStudent_Found() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void testDeleteStudent_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Student not found")).when(studentService).deleteStudent(99L);

        mockMvc.perform(delete("/students/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student not found"));
    }
}