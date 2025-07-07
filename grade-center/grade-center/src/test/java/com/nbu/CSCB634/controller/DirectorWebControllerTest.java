package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.DirectorService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.auth.UserService;
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
class DirectorWebControllerTest {

    @Mock
    private DirectorService directorService;

    @Mock
    private UserService userService;

    @Mock
    private SchoolService schoolService;

    @InjectMocks
    private DirectorWebController directorWebController;

    private MockMvc mockMvc;

    private Director director;
    private User user;
    private School school;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(directorWebController).build();

        user = User.builder()
                .id(1L)
                .username("director1")
                .firstName("John")
                .lastName("Director")
                .email("director@test.com")
                .build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        director = Director.builder()
                .id(1L)
                .user(user)
                .school(school)
                .build();
    }

    @Test
    void testListDirectors_Success() throws Exception {
        when(directorService.getAllDirectors()).thenReturn(List.of(director));

        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/list"))
                .andExpect(model().attributeExists("directors"));

        verify(directorService, times(1)).getAllDirectors();
    }

    @Test
    void testListDirectors_EmptyList() throws Exception {
        when(directorService.getAllDirectors()).thenReturn(List.of());

        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/list"))
                .andExpect(model().attributeExists("directors"));

        verify(directorService, times(1)).getAllDirectors();
    }

    @Test
    void testCreateDirectorForm_Success() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(get("/directors/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/form"))
                .andExpect(model().attributeExists("director"))
                .andExpect(model().attributeExists("schools"));

        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testCreateDirector_Success() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(directorService.createDirector(any(Director.class))).thenReturn(director);

        mockMvc.perform(post("/directors/create")
                .param("username", "director1")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/directors"));

        verify(userService, times(1)).registerUser(any(User.class));
        verify(directorService, times(1)).createDirector(any(Director.class));
    }

    @Test
    void testCreateDirector_UserRegistrationFallback() throws Exception {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("User exists"))
                .thenReturn(user);
        when(userService.getNextAvailableId()).thenReturn(2L);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(directorService.createDirector(any(Director.class))).thenReturn(director);

        mockMvc.perform(post("/directors/create")
                .param("username", "director1")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/directors"));

        verify(userService, times(2)).registerUser(any(User.class));
        verify(userService, times(1)).getNextAvailableId();
    }

    @Test
    void testCreateDirector_DirectorCreationFallback() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(directorService.createDirector(any(Director.class)))
                .thenThrow(new RuntimeException("Director exists"))
                .thenReturn(director);
        when(directorService.getNextAvailableId()).thenReturn(2L);

        mockMvc.perform(post("/directors/create")
                .param("username", "director1")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/directors"));

        verify(directorService, times(2)).createDirector(any(Director.class));
        verify(directorService, times(1)).getNextAvailableId();
    }

    @Test
    void testCreateDirector_SchoolNotFound() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(post("/directors/create")
                .param("username", "director1")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "99"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/form"));

        verify(schoolService, times(1)).getSchoolById(99L);
        verify(directorService, never()).createDirector(any(Director.class));
    }

    @Test
    void testCreateDirector_WithException() throws Exception {
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("Fatal error"));
        when(userService.getNextAvailableId()).thenReturn(2L);
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(post("/directors/create")
                .param("username", "director1")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/form"));

        verify(userService, times(2)).registerUser(any(User.class));
        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testViewDirector_Success() throws Exception {
        when(directorService.getDirectorById(1L)).thenReturn(Optional.of(director));

        mockMvc.perform(get("/directors/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/view"))
                .andExpect(model().attributeExists("director"));

        verify(directorService, times(1)).getDirectorById(1L);
    }

    @Test
    void testViewDirector_NotFound() throws Exception {
        when(directorService.getDirectorById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/directors/99"))
                .andExpect(status().is5xxServerError());

        verify(directorService, times(1)).getDirectorById(99L);
    }

    @Test
    void testEditDirectorForm_Success() throws Exception {
        when(directorService.getDirectorById(1L)).thenReturn(Optional.of(director));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(get("/directors/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/form"))
                .andExpect(model().attributeExists("director"))
                .andExpect(model().attributeExists("schools"));

        verify(directorService, times(1)).getDirectorById(1L);
        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testEditDirectorForm_NotFound() throws Exception {
        when(directorService.getDirectorById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/directors/99/edit"))
                .andExpect(status().is5xxServerError());

        verify(directorService, times(1)).getDirectorById(99L);
    }

    @Test
    void testUpdateDirector_Success() throws Exception {
        when(directorService.getDirectorById(1L)).thenReturn(Optional.of(director));
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(directorService.updateDirector(anyLong(), any(Director.class))).thenReturn(director);

        mockMvc.perform(post("/directors/1/edit")
                .param("firstName", "John Updated")
                .param("lastName", "Director Updated")
                .param("email", "updated@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/directors"));

        verify(directorService, times(1)).updateDirector(anyLong(), any(Director.class));
    }

    @Test
    void testUpdateDirector_NotFound() throws Exception {
        when(directorService.getDirectorById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/directors/99/edit")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "1"))
                .andExpect(status().is5xxServerError());

        verify(directorService, times(1)).getDirectorById(99L);
        verify(directorService, never()).updateDirector(anyLong(), any(Director.class));
    }

    @Test
    void testUpdateDirector_SchoolNotFound() throws Exception {
        when(directorService.getDirectorById(1L)).thenReturn(Optional.of(director));
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(post("/directors/1/edit")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "99"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/form"));

        verify(schoolService, times(1)).getSchoolById(99L);
        verify(directorService, never()).updateDirector(anyLong(), any(Director.class));
    }

    @Test
    void testUpdateDirector_WithException() throws Exception {
        when(directorService.getDirectorById(1L)).thenReturn(Optional.of(director));
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(directorService.updateDirector(anyLong(), any(Director.class)))
                .thenThrow(new RuntimeException("Update failed"));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));

        mockMvc.perform(post("/directors/1/edit")
                .param("firstName", "John")
                .param("lastName", "Director")
                .param("email", "director@test.com")
                .param("schoolId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("directors/form"));

        verify(directorService, times(1)).updateDirector(anyLong(), any(Director.class));
        verify(schoolService, times(1)).getAllSchools();
    }

    @Test
    void testDeleteDirector_Success() throws Exception {
        doNothing().when(directorService).deleteDirector(1L);

        mockMvc.perform(post("/directors/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/directors"));

        verify(directorService, times(1)).deleteDirector(1L);
    }

    @Test
    void testDeleteDirector_WithException() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(directorService).deleteDirector(1L);

        mockMvc.perform(post("/directors/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/directors"));

        verify(directorService, times(1)).deleteDirector(1L);
    }
} 