package com.nbu.CSCB634.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.DirectorService;
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

@WebMvcTest(DirectorController.class)
class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectorService directorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Director director;

    @BeforeEach
    void setup() {
        User u = User.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        director = Director.builder()
                .user(u)
                .build();
    }

    @Test
    void testGetDirectorById_Found() throws Exception {
        when(directorService.getDirectorById(1L)).thenReturn(Optional.of(director));

        mockMvc.perform(get("/api/directors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetDirectorById_NotFound() throws Exception {
        when(directorService.getDirectorById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/directors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDirectors() throws Exception {
        User u = User.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        Director director2 = Director.builder().id(2L).user(u).build();
        when(directorService.getAllDirectors()).thenReturn(List.of(director, director2));

        mockMvc.perform(get("/api/directors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testCreateDirector() throws Exception {
        when(directorService.createDirector(any(Director.class))).thenReturn(director);

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testUpdateDirector_Found() throws Exception {
        User u = User.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        Director updated = Director.builder()
                .id(1L)
                .user(u)
                .build();

        when(directorService.updateDirector(eq(1L), any(Director.class))).thenReturn(updated);

        mockMvc.perform(put("/api/directors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John Updated"));
    }

    @Test
    void testUpdateDirector_NotFound() throws Exception {
        when(directorService.updateDirector(eq(99L), any(Director.class)))
                .thenThrow(new IllegalArgumentException("Director not found"));

        mockMvc.perform(put("/api/directors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Director not found"));
    }

    @Test
    void testDeleteDirector_Found() throws Exception {
        doNothing().when(directorService).deleteDirector(1L);

        mockMvc.perform(delete("/api/directors/1"))
                .andExpect(status().isNoContent());

        verify(directorService).deleteDirector(1L);
    }

    @Test
    void testDeleteDirector_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Director not found")).when(directorService).deleteDirector(99L);

        mockMvc.perform(delete("/api/directors/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Director not found"));
    }
}