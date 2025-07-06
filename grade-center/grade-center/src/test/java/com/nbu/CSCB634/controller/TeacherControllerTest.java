package com.nbu.CSCB634.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Teacher teacher;

    @BeforeEach
    void setup() {
        User u = User.builder()
                .firstName("Robert")
                .lastName("Langdon")
                .build();
        teacher = Teacher.builder()
                .id(1L)
                .user(u)
                .build();
    }

    @Test
    void testGetTeacherById_Found() throws Exception {
        when(teacherService.getTeacherById(1L)).thenReturn(Optional.of(teacher));

        mockMvc.perform(get("/teachers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Robert"));
    }

    @Test
    void testGetTeacherById_NotFound() throws Exception {
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teachers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllTeachers() throws Exception {
        User u = User.builder()
                .firstName("Leonardo").lastName("Da Vinci")
                .build();
        Teacher t2 = Teacher.builder().id(2L).user(u).build();

        when(teacherService.getAllTeachers()).thenReturn(List.of(teacher, t2));

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCreateTeacher() throws Exception {
        when(teacherService.createTeacher(any(Teacher.class))).thenReturn(teacher);

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Robert"));
    }

    @Test
    void testUpdateTeacher_Found() throws Exception {
        User u = User.builder()
                .firstName("Robert Updated")
                .lastName("Langdon Updated")
                .build();
        Teacher updated = Teacher.builder()
                .id(1L)
                .user(u)
                .build();

        when(teacherService.updateTeacher(eq(1L), any(Teacher.class))).thenReturn(updated);

        mockMvc.perform(put("/teachers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Robert Updated"));
    }

    @Test
    void testUpdateTeacher_NotFound() throws Exception {
        when(teacherService.updateTeacher(eq(99L), any(Teacher.class)))
                .thenThrow(new IllegalArgumentException("Teacher not found"));

        mockMvc.perform(put("/teachers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Teacher not found"));
    }

    @Test
    void testDeleteTeacher_Found() throws Exception {
        doNothing().when(teacherService).deleteTeacher(1L);

        mockMvc.perform(delete("/teachers/1"))
                .andExpect(status().isNoContent());

        verify(teacherService).deleteTeacher(1L);
    }

    @Test
    void testDeleteTeacher_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Teacher not found")).when(teacherService).deleteTeacher(99L);

        mockMvc.perform(delete("/teachers/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Teacher not found"));
    }
}