package com.nbu.CSCB634.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.service.ParentService;
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

class ParentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParentService parentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Parent parent;

    @BeforeEach
    void setup() {
        parent = Parent.builder()
                .id(1L)
                .firstName("Mary")
                .lastName("Poppins")
                .build();
    }

    @Test
    void testGetParentById_Found() throws Exception {
        when(parentService.getParentById(1L)).thenReturn(Optional.of(parent));

        mockMvc.perform(get("/parents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Mary"));
    }

    @Test
    void testGetParentById_NotFound() throws Exception {
        when(parentService.getParentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/parents/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllParents() throws Exception {
        Parent p2 = Parent.builder().id(2L).firstName("John").lastName("Doe").build();

        when(parentService.getAllParents()).thenReturn(List.of(parent, p2));

        mockMvc.perform(get("/parents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCreateParent() throws Exception {
        when(parentService.createParent(any(Parent.class))).thenReturn(parent);

        mockMvc.perform(post("/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Mary"));
    }

    @Test
    void testUpdateParent_Found() throws Exception {
        Parent updated = Parent.builder()
                .id(1L)
                .firstName("Mary Updated")
                .lastName("Poppins Updated")
                .build();

        when(parentService.updateParent(eq(1L), any(Parent.class))).thenReturn(updated);

        mockMvc.perform(put("/parents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mary Updated"));
    }

    @Test
    void testUpdateParent_NotFound() throws Exception {
        when(parentService.updateParent(eq(99L), any(Parent.class)))
                .thenThrow(new IllegalArgumentException("Parent not found"));

        mockMvc.perform(put("/parents/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parent)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Parent not found"));
    }

    @Test
    void testDeleteParent_Found() throws Exception {
        doNothing().when(parentService).deleteParent(1L);

        mockMvc.perform(delete("/parents/1"))
                .andExpect(status().isNoContent());

        verify(parentService).deleteParent(1L);
    }

    @Test
    void testDeleteParent_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Parent not found")).when(parentService).deleteParent(99L);

        mockMvc.perform(delete("/parents/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Parent not found"));
    }
}