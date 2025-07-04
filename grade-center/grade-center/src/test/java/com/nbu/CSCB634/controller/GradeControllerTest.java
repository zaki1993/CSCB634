package com.nbu.CSCB634.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.service.GradeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradeService gradeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Grade grade;
    private Student student;

    @BeforeEach
    void setup() {
        student = Student.builder().id(1L).firstName("Alice").lastName("Smith").build();
        Subject subject = Subject.builder().id(1L).name("Math").build();

        grade = Grade.builder()
                .id(1L)
                .student(student)
                .subject(subject)
                .value(5)
                .dateAwarded(LocalDate.now())
                .build();
    }

    @Test
    void testGetGradeById_Found() throws Exception {
        when(gradeService.getGradeById(1L)).thenReturn(Optional.of(grade));

        mockMvc.perform(get("/grades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.value").value(5));
    }

    @Test
    void testGetGradeById_NotFound() throws Exception {
        when(gradeService.getGradeById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/grades/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGradesByStudentId_Found() throws Exception {
        when(gradeService.getGradesByStudentId(1L)).thenReturn(List.of(grade));

        mockMvc.perform(get("/grades/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetGradesByStudentId_StudentNotFound() throws Exception {
        when(gradeService.getGradesByStudentId(99L))
                .thenThrow(new IllegalArgumentException("Student not found"));

        mockMvc.perform(get("/grades/student/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student not found"));
    }

    @Test
    void testCreateGrade() throws Exception {
        when(gradeService.createGrade(any(Grade.class))).thenReturn(grade);

        mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grade)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(5));
    }

    @Test
    void testUpdateGrade_Found() throws Exception {
        Grade updated = Grade.builder()
                .id(1L)
                .student(student)
                .subject(Subject.builder().id(2L).name("Physics").build())
                .value(4)
                .dateAwarded(LocalDate.now().plusDays(1))
                .build();

        when(gradeService.updateGrade(eq(1L), any(Grade.class))).thenReturn(updated);

        mockMvc.perform(put("/grades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(4))
                .andExpect(jsonPath("$.subject.id").value(2));
    }

    @Test
    void testUpdateGrade_NotFound() throws Exception {
        when(gradeService.updateGrade(eq(99L), any(Grade.class)))
                .thenThrow(new IllegalArgumentException("Grade not found"));

        mockMvc.perform(put("/grades/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grade)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Grade not found"));
    }

    @Test
    void testDeleteGrade_Found() throws Exception {
        doNothing().when(gradeService).deleteGrade(1L);

        mockMvc.perform(delete("/grades/1"))
                .andExpect(status().isNoContent());

        verify(gradeService).deleteGrade(1L);
    }

    @Test
    void testDeleteGrade_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Grade not found")).when(gradeService).deleteGrade(99L);

        mockMvc.perform(delete("/grades/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Grade not found"));
    }
}