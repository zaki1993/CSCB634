package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.AcademicTerm;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.service.AcademicTermService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.SubjectService;
import com.nbu.CSCB634.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AcademicTermWebControllerTest {

    @Mock
    private AcademicTermService academicTermService;

    @Mock
    private SchoolService schoolService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private AcademicTermWebController academicTermWebController;

    private MockMvc mockMvc;

    private AcademicTerm academicTerm;
    private School school;
    private Subject subject;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(academicTermWebController).build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        subject = Subject.builder()
                .id(1L)
                .name("Mathematics")
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .school(school)
                .build();

        academicTerm = AcademicTerm.builder()
                .id(1L)
                .name("Term 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .school(school)
                .subjects(Set.of(subject))
                .teachers(Set.of(teacher))
                .build();
    }

    @Test
    void testListAcademicTerms_Success() throws Exception {
        when(academicTermService.getAllAcademicTerms()).thenReturn(List.of(academicTerm));

        mockMvc.perform(get("/academic-terms"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-terms/list"))
                .andExpect(model().attributeExists("academicTerms"));

        verify(academicTermService, times(1)).getAllAcademicTerms();
    }

    @Test
    void testListAcademicTerms_EmptyList() throws Exception {
        when(academicTermService.getAllAcademicTerms()).thenReturn(List.of());

        mockMvc.perform(get("/academic-terms"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-terms/list"))
                .andExpect(model().attributeExists("academicTerms"));

        verify(academicTermService, times(1)).getAllAcademicTerms();
    }

    @Test
    void testViewAcademicTerm_Success() throws Exception {
        when(academicTermService.getAcademicTermById(1L)).thenReturn(Optional.of(academicTerm));

        mockMvc.perform(get("/academic-terms/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-terms/view"))
                .andExpect(model().attributeExists("academicTerm"));

        verify(academicTermService, times(1)).getAcademicTermById(1L);
    }

    @Test
    void testCreateAcademicTermForm_Success() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(subjectService.getAllSubjects()).thenReturn(List.of(subject));
        when(teacherService.getAllTeachers()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/academic-terms/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-terms/form"))
                .andExpect(model().attributeExists("academicTermDto"))
                .andExpect(model().attributeExists("schools"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("teachers"));

        verify(schoolService, times(1)).getAllSchools();
        verify(subjectService, times(1)).getAllSubjects();
        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    void testCreateAcademicTerm_Success() throws Exception {
        when(schoolService.getSchoolById(anyLong())).thenReturn(Optional.of(school));
        when(subjectService.getSubjectById(anyLong())).thenReturn(Optional.of(subject));
        when(teacherService.getTeacherById(anyLong())).thenReturn(Optional.of(teacher));
        when(academicTermService.createAcademicTerm(any(AcademicTerm.class))).thenReturn(academicTerm);

        mockMvc.perform(post("/academic-terms/create")
                .param("name", "Term 1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30")
                .param("schoolId", "1")
                .param("subjectIds", "1")
                .param("teacherIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testCreateAcademicTerm_WithException() throws Exception {
        when(academicTermService.createAcademicTerm(any(AcademicTerm.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/academic-terms/create")
                .param("name", "Term 1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testEditAcademicTermForm_Success() throws Exception {
        when(academicTermService.getAcademicTermById(1L)).thenReturn(Optional.of(academicTerm));
        when(schoolService.getAllSchools()).thenReturn(List.of(school));
        when(subjectService.getAllSubjects()).thenReturn(List.of(subject));
        when(teacherService.getAllTeachers()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/academic-terms/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-terms/form"))
                .andExpect(model().attributeExists("academicTermDto"))
                .andExpect(model().attributeExists("schools"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("teachers"));

        verify(academicTermService, times(1)).getAcademicTermById(1L);
    }
    @Test
    void testUpdateAcademicTerm_Success() throws Exception {
        when(schoolService.getSchoolById(anyLong())).thenReturn(Optional.of(school));
        when(subjectService.getSubjectById(anyLong())).thenReturn(Optional.of(subject));
        when(teacherService.getTeacherById(anyLong())).thenReturn(Optional.of(teacher));
        when(academicTermService.updateAcademicTerm(anyLong(), any(AcademicTerm.class))).thenReturn(academicTerm);

        mockMvc.perform(post("/academic-terms/edit/1")
                .param("name", "Updated Term")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30")
                .param("schoolId", "1")
                .param("subjectIds", "1")
                .param("teacherIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).updateAcademicTerm(anyLong(), any(AcademicTerm.class));
    }

    @Test
    void testUpdateAcademicTerm_WithException() throws Exception {
        when(academicTermService.updateAcademicTerm(anyLong(), any(AcademicTerm.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(post("/academic-terms/edit/1")
                .param("name", "Updated Term")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).updateAcademicTerm(anyLong(), any(AcademicTerm.class));
    }

    @Test
    void testDeleteAcademicTerm_Success() throws Exception {
        doNothing().when(academicTermService).deleteAcademicTerm(1L);

        mockMvc.perform(post("/academic-terms/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).deleteAcademicTerm(1L);
    }

    @Test
    void testDeleteAcademicTerm_WithException() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(academicTermService).deleteAcademicTerm(1L);

        mockMvc.perform(post("/academic-terms/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).deleteAcademicTerm(1L);
    }

    @Test
    void testCreateAcademicTerm_WithoutOptionalFields() throws Exception {
        when(academicTermService.createAcademicTerm(any(AcademicTerm.class))).thenReturn(academicTerm);

        mockMvc.perform(post("/academic-terms/create")
                .param("name", "Simple Term")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(academicTermService, times(1)).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testCreateAcademicTerm_SchoolNotFound() throws Exception {
        when(schoolService.getSchoolById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/academic-terms/create")
                .param("name", "Term 1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30")
                .param("schoolId", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(schoolService, times(1)).getSchoolById(99L);
        verify(academicTermService, never()).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testCreateAcademicTerm_SubjectNotFound() throws Exception {
        when(schoolService.getSchoolById(anyLong())).thenReturn(Optional.of(school));
        when(subjectService.getSubjectById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/academic-terms/create")
                .param("name", "Term 1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30")
                .param("schoolId", "1")
                .param("subjectIds", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(subjectService, times(1)).getSubjectById(99L);
        verify(academicTermService, never()).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testCreateAcademicTerm_TeacherNotFound() throws Exception {
        when(schoolService.getSchoolById(anyLong())).thenReturn(Optional.of(school));
        when(subjectService.getSubjectById(anyLong())).thenReturn(Optional.of(subject));
        when(teacherService.getTeacherById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/academic-terms/create")
                .param("name", "Term 1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30")
                .param("schoolId", "1")
                .param("subjectIds", "1")
                .param("teacherIds", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/academic-terms"));

        verify(teacherService, times(1)).getTeacherById(99L);
        verify(academicTermService, never()).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testCreateAcademicTermForm_EmptyLists() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of());
        when(subjectService.getAllSubjects()).thenReturn(List.of());
        when(teacherService.getAllTeachers()).thenReturn(List.of());

        mockMvc.perform(get("/academic-terms/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("academic-terms/form"))
                .andExpect(model().attributeExists("schools"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("teachers"));
    }
} 