package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.service.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class SchoolWebControllerTest {

    @Mock
    private SchoolService schoolService;

    @InjectMocks
    private SchoolWebController schoolWebController;

    private MockMvc mockMvc;

    private School school;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(schoolWebController).build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("Test Address")
                .build();
    }

    @Test
    void testListSchools_Success() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(get("/schools"))
                .andExpect(status().isOk())
                .andExpect(view().name("schools/list"))
                .andExpect(model().attributeExists("schools"));

        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testListSchools_EmptyList() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of());

        mockMvc.perform(get("/schools"))
                .andExpect(status().isOk())
                .andExpect(view().name("schools/list"))
                .andExpect(model().attributeExists("schools"));

        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testShowCreateForm_Success() throws Exception {
        mockMvc.perform(get("/schools/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("schools/form"))
                .andExpect(model().attributeExists("school"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    void testCreateSchool_Success() throws Exception {
        when(schoolService.createSchool(any(School.class))).thenReturn(school);

        mockMvc.perform(post("/schools/new")
                .param("name", "Test School")
                .param("address", "Test Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).createSchool(any(School.class));
    }

    @Test
    void testCreateSchool_WithException() throws Exception {
        when(schoolService.createSchool(any(School.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/schools/new")
                .param("name", "Test School")
                .param("address", "Test Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools/new"));

        verify(schoolService, times(1)).createSchool(any(School.class));
    }

    @Test
    void testShowEditForm_Success() throws Exception {
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));

        mockMvc.perform(get("/schools/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("schools/form"))
                .andExpect(model().attributeExists("school"))
                .andExpect(model().attribute("isEdit", true));

        verify(schoolService, times(1)).getSchoolById(1L);
    }

    @Test
    void testShowEditForm_NotFound() throws Exception {
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/schools/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).getSchoolById(99L);
    }

    @Test
    void testUpdateSchool_Success() throws Exception {
        when(schoolService.updateSchool(anyLong(), any(School.class))).thenReturn(school);

        mockMvc.perform(post("/schools/1/edit")
                .param("name", "Updated School")
                .param("address", "Updated Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).updateSchool(anyLong(), any(School.class));
    }

    @Test
    void testUpdateSchool_NotFound() throws Exception {
        when(schoolService.updateSchool(anyLong(), any(School.class)))
                .thenThrow(new IllegalArgumentException("School not found"));

        mockMvc.perform(post("/schools/1/edit")
                .param("name", "Updated School")
                .param("address", "Updated Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).updateSchool(anyLong(), any(School.class));
    }

    @Test
    void testUpdateSchool_WithException() throws Exception {
        when(schoolService.updateSchool(anyLong(), any(School.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/schools/1/edit")
                .param("name", "Updated School")
                .param("address", "Updated Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools/1/edit"));

        verify(schoolService, times(1)).updateSchool(anyLong(), any(School.class));
    }

    @Test
    void testDeleteSchool_Success() throws Exception {
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        doNothing().when(schoolService).deleteSchool(1L);

        mockMvc.perform(post("/schools/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).getSchoolById(1L);
        verify(schoolService, times(1)).deleteSchool(1L);
    }

    @Test
    void testDeleteSchool_NotFound() throws Exception {
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/schools/99/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).getSchoolById(99L);
        verify(schoolService, never()).deleteSchool(anyLong());
    }

    @Test
    void testDeleteSchool_WithException() throws Exception {
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        doThrow(new RuntimeException("Delete failed")).when(schoolService).deleteSchool(1L);

        mockMvc.perform(post("/schools/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).deleteSchool(1L);
    }

    @Test
    void testViewSchool_Success() throws Exception {
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));

        mockMvc.perform(get("/schools/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("schools/view"))
                .andExpect(model().attributeExists("school"));

        verify(schoolService, times(1)).getSchoolById(1L);
    }

    @Test
    void testViewSchool_NotFound() throws Exception {
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/schools/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schools"));

        verify(schoolService, times(1)).getSchoolById(99L);
    }
} 