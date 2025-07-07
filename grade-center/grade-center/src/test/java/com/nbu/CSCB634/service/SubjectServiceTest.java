package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        subject = Subject.builder()
                .id(1L)
                .name("Mathematics")
                .build();
    }

    @Test
    void testGetAll_Success() {
        when(subjectRepository.findAll()).thenReturn(List.of(subject));

        List<Subject> result = subjectService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mathematics");
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        when(subjectRepository.findAll()).thenReturn(List.of());

        List<Subject> result = subjectService.getAll();

        assertThat(result).isEmpty();
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSubjects_Success() {
        when(subjectRepository.findAll()).thenReturn(List.of(subject));

        List<Subject> result = subjectService.getAllSubjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mathematics");
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSubjects_EmptyList() {
        when(subjectRepository.findAll()).thenReturn(List.of());

        List<Subject> result = subjectService.getAllSubjects();

        assertThat(result).isEmpty();
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetSubjectById_Found() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        Optional<Subject> result = subjectService.getSubjectById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Mathematics");
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSubjectById_NotFound() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Subject> result = subjectService.getSubjectById(99L);

        assertThat(result).isEmpty();
        verify(subjectRepository, times(1)).findById(99L);
    }

    @Test
    void testCreateSubject_Success() {
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        Subject result = subjectService.createSubject(subject);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mathematics");
        verify(subjectRepository, times(1)).save(subject);
    }

    @Test
    void testCreateSubject_WithNullName() {
        Subject nullNameSubject = Subject.builder()
                .id(2L)
                .name(null)
                .build();

        when(subjectRepository.save(any(Subject.class))).thenReturn(nullNameSubject);

        Subject result = subjectService.createSubject(nullNameSubject);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
        verify(subjectRepository, times(1)).save(nullNameSubject);
    }

    @Test
    void testUpdateSubject_Success() {
        Subject updatedSubject = Subject.builder()
                .name("Physics")
                .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        Subject result = subjectService.updateSubject(1L, updatedSubject);

        assertThat(result).isNotNull();
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(subject);
    }

    @Test
    void testUpdateSubject_NotFound() {
        Subject updatedSubject = Subject.builder()
                .name("Physics")
                .build();

        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.updateSubject(99L, updatedSubject))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subject not found");

        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    void testUpdateSubject_WithNullName() {
        Subject updatedSubject = Subject.builder()
                .name(null)
                .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        Subject result = subjectService.updateSubject(1L, updatedSubject);

        assertThat(result).isNotNull();
        verify(subjectRepository, times(1)).save(subject);
    }

    @Test
    void testDeleteSubject_Success() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        doNothing().when(subjectRepository).delete(subject);

        subjectService.deleteSubject(1L);

        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).delete(subject);
    }

    @Test
    void testDeleteSubject_NotFound() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.deleteSubject(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subject not found");

        verify(subjectRepository, never()).delete(any(Subject.class));
    }

    @Test
    void testGetAll_WithMultipleSubjects() {
        Subject subject2 = Subject.builder()
                .id(2L)
                .name("Physics")
                .build();
        Subject subject3 = Subject.builder()
                .id(3L)
                .name("Chemistry")
                .build();

        when(subjectRepository.findAll()).thenReturn(List.of(subject, subject2, subject3));

        List<Subject> result = subjectService.getAll();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Mathematics");
        assertThat(result.get(1).getName()).isEqualTo("Physics");
        assertThat(result.get(2).getName()).isEqualTo("Chemistry");
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testCreateSubject_WithEmptyName() {
        Subject emptyNameSubject = Subject.builder()
                .id(2L)
                .name("")
                .build();

        when(subjectRepository.save(any(Subject.class))).thenReturn(emptyNameSubject);

        Subject result = subjectService.createSubject(emptyNameSubject);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("");
        verify(subjectRepository, times(1)).save(emptyNameSubject);
    }

    @Test
    void testUpdateSubject_WithEmptyName() {
        Subject updatedSubject = Subject.builder()
                .name("")
                .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        Subject result = subjectService.updateSubject(1L, updatedSubject);

        assertThat(result).isNotNull();
        verify(subjectRepository, times(1)).save(subject);
    }

    @Test
    void testCreateSubject_WithLongName() {
        String longName = "Mathematics and Advanced Calculus with Integration and Differential Equations";
        Subject longNameSubject = Subject.builder()
                .id(2L)
                .name(longName)
                .build();

        when(subjectRepository.save(any(Subject.class))).thenReturn(longNameSubject);

        Subject result = subjectService.createSubject(longNameSubject);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(longName);
        verify(subjectRepository, times(1)).save(longNameSubject);
    }

    @Test
    void testUpdateSubject_ChangeName() {
        String newName = "Advanced Mathematics";
        Subject updatedSubject = Subject.builder()
                .name(newName)
                .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject savedSubject = invocation.getArgument(0);
            savedSubject.setName(newName);
            return savedSubject;
        });

        Subject result = subjectService.updateSubject(1L, updatedSubject);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newName);
        verify(subjectRepository, times(1)).save(subject);
    }

    @Test
    void testGetSubjectById_WithNullId() {
        when(subjectRepository.findById(null)).thenReturn(Optional.empty());

        Optional<Subject> result = subjectService.getSubjectById(null);

        assertThat(result).isEmpty();
        verify(subjectRepository, times(1)).findById(null);
    }
} 