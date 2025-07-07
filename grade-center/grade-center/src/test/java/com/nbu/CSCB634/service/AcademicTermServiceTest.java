package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.AcademicTerm;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.AcademicTermRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AcademicTermServiceTest {

    @Mock
    private AcademicTermRepository academicTermRepository;

    @InjectMocks
    private AcademicTermService academicTermService;

    private AcademicTerm academicTerm;
    private School school;
    private Subject subject;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
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
                .subjects(List.of(subject))
                .teachers(List.of(teacher))
                .build();
    }

    @Test
    void testCreateAcademicTerm_Success() {
        when(academicTermRepository.save(any(AcademicTerm.class))).thenReturn(academicTerm);

        AcademicTerm result = academicTermService.createAcademicTerm(academicTerm);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Term 1");
        verify(academicTermRepository, times(1)).save(academicTerm);
    }

    @Test
    void testGetAcademicTermById_Found() {
        when(academicTermRepository.findById(1L)).thenReturn(Optional.of(academicTerm));

        Optional<AcademicTerm> result = academicTermService.getAcademicTermById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Term 1");
    }

    @Test
    void testGetAcademicTermById_NotFound() {
        when(academicTermRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AcademicTerm> result = academicTermService.getAcademicTermById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllAcademicTerms_Success() {
        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm));

        List<AcademicTerm> result = academicTermService.getAllAcademicTerms();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Term 1");
    }

    @Test
    void testGetAllAcademicTerms_EmptyList() {
        when(academicTermRepository.findAll()).thenReturn(List.of());

        List<AcademicTerm> result = academicTermService.getAllAcademicTerms();

        assertThat(result).isEmpty();
    }

    @Test
    void testGetAcademicTermsBySchoolId_Success() {
        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm));

        List<AcademicTerm> result = academicTermService.getAcademicTermsBySchoolId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSchool().getId()).isEqualTo(1L);
    }

    @Test
    void testGetAcademicTermsBySchoolId_DifferentSchool() {
        AcademicTerm differentSchoolTerm = AcademicTerm.builder()
                .id(2L)
                .name("Term 2")
                .school(School.builder().id(2L).name("Other School").build())
                .build();

        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm, differentSchoolTerm));

        List<AcademicTerm> result = academicTermService.getAcademicTermsBySchoolId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSchool().getId()).isEqualTo(1L);
    }

    @Test
    void testGetAcademicTermsBySchoolId_NullSchool() {
        AcademicTerm nullSchoolTerm = AcademicTerm.builder()
                .id(2L)
                .name("Term 2")
                .school(null)
                .build();

        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm, nullSchoolTerm));

        List<AcademicTerm> result = academicTermService.getAcademicTermsBySchoolId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSchool().getId()).isEqualTo(1L);
    }

    @Test
    void testGetAcademicTermsBySchoolId_NoMatchingSchool() {
        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm));

        List<AcademicTerm> result = academicTermService.getAcademicTermsBySchoolId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateAcademicTerm_Success() {
        AcademicTerm updatedTerm = AcademicTerm.builder()
                .name("Updated Term")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(7))
                .school(school)
                .subjects(List.of(subject))
                .teachers(List.of(teacher))
                .build();

        when(academicTermRepository.findById(1L)).thenReturn(Optional.of(academicTerm));
        when(academicTermRepository.save(any(AcademicTerm.class))).thenReturn(academicTerm);

        AcademicTerm result = academicTermService.updateAcademicTerm(1L, updatedTerm);

        assertThat(result).isNotNull();
        verify(academicTermRepository, times(1)).save(academicTerm);
    }

    @Test
    void testUpdateAcademicTerm_NotFound() {
        AcademicTerm updatedTerm = new AcademicTerm();
        when(academicTermRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> academicTermService.updateAcademicTerm(99L, updatedTerm))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AcademicTerm not found");
    }

    @Test
    void testDeleteAcademicTerm_Success() {
        when(academicTermRepository.findById(1L)).thenReturn(Optional.of(academicTerm));
        doNothing().when(academicTermRepository).delete(academicTerm);

        academicTermService.deleteAcademicTerm(1L);

        verify(academicTermRepository, times(1)).delete(academicTerm);
    }

    @Test
    void testDeleteAcademicTerm_NotFound() {
        when(academicTermRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> academicTermService.deleteAcademicTerm(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AcademicTerm not found");
    }

    @Test
    void testGetAll_Success() {
        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm));

        List<AcademicTerm> result = academicTermService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Term 1");
    }

    @Test
    void testGetAll_EmptyList() {
        when(academicTermRepository.findAll()).thenReturn(List.of());

        List<AcademicTerm> result = academicTermService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void testCreateAcademicTerm_WithNullFields() {
        AcademicTerm termWithNullFields = AcademicTerm.builder()
                .id(2L)
                .name("Term with nulls")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .school(null)
                .subjects(null)
                .teachers(null)
                .build();

        when(academicTermRepository.save(any(AcademicTerm.class))).thenReturn(termWithNullFields);

        AcademicTerm result = academicTermService.createAcademicTerm(termWithNullFields);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getSchool()).isNull();
        assertThat(result.getSubjects()).isNull();
        assertThat(result.getTeachers()).isNull();
    }

    @Test
    void testUpdateAcademicTerm_WithNullFields() {
        AcademicTerm updatedTerm = AcademicTerm.builder()
                .name("Updated Term")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(7))
                .school(null)
                .subjects(null)
                .teachers(null)
                .build();

        when(academicTermRepository.findById(1L)).thenReturn(Optional.of(academicTerm));
        when(academicTermRepository.save(any(AcademicTerm.class))).thenReturn(academicTerm);

        AcademicTerm result = academicTermService.updateAcademicTerm(1L, updatedTerm);

        assertThat(result).isNotNull();
        verify(academicTermRepository, times(1)).save(academicTerm);
    }

    @Test
    void testGetAcademicTermsBySchoolId_WithMultipleMatchingTerms() {
        AcademicTerm term2 = AcademicTerm.builder()
                .id(2L)
                .name("Term 2")
                .school(school)
                .build();

        when(academicTermRepository.findAll()).thenReturn(List.of(academicTerm, term2));

        List<AcademicTerm> result = academicTermService.getAcademicTermsBySchoolId(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSchool().getId()).isEqualTo(1L);
        assertThat(result.get(1).getSchool().getId()).isEqualTo(1L);
    }
} 