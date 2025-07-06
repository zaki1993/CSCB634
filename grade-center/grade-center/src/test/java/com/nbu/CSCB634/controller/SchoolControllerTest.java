package com.nbu.CSCB634.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.service.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchoolControllerTest.class)
@ActiveProfiles("test")
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchoolService schoolService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private School school;

    @BeforeEach
    void setup() {
        school = School.builder()
                .id(1L)
                .name("Springfield Elementary")
                .address("742 Evergreen Terrace")
                .build();
    }

    @Test
    void testGetSchoolById_Found() throws Exception {
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));

        mockMvc.perform(get("/schools/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Springfield Elementary"));
    }

    @Test
    void testGetSchoolById_NotFound() throws Exception {
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/schools/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllSchools() throws Exception {
        School school2 = School.builder().id(2L).name("Shelbyville Elementary").address("123 Shelbyville").build();
        when(schoolService.getAllSchools()).thenReturn(List.of(school, school2));

        mockMvc.perform(get("/schools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCreateSchool() throws Exception {
        when(schoolService.createSchool(any(School.class))).thenReturn(school);

        mockMvc.perform(post("/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(school)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Springfield Elementary"));
    }

    @Test
    void testUpdateSchool_Found() throws Exception {
        School updated = School.builder()
                .id(1L)
                .name("Springfield Elementary Updated")
                .address("742 Evergreen Terrace Updated")
                .build();

        when(schoolService.updateSchool(eq(1L), any(School.class))).thenReturn(updated);

        mockMvc.perform(put("/schools/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Springfield Elementary Updated"));
    }

    @Test
    void testUpdateSchool_NotFound() throws Exception {
        when(schoolService.updateSchool(eq(99L), any(School.class)))
                .thenThrow(new IllegalArgumentException("School not found"));

        mockMvc.perform(put("/schools/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(school)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("School not found"));
    }

    @Test
    void testDeleteSchool_Found() throws Exception {
        doNothing().when(schoolService).deleteSchool(1L);

        mockMvc.perform(delete("/schools/1"))
                .andExpect(status().isNoContent());

        verify(schoolService).deleteSchool(1L);
    }

    @Test
    void testDeleteSchool_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("School not found")).when(schoolService).deleteSchool(99L);

        mockMvc.perform(delete("/schools/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("School not found"));
    }
}