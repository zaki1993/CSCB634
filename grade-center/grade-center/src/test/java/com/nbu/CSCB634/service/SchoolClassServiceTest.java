package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.repository.SchoolClassRepository;
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

class SchoolClassServiceTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @InjectMocks
    private SchoolClassService schoolClassService;

    private SchoolClass schoolClass;
    private School school;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("Test Address")
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .name("10A")
                .gradeNumber(10)
                .school(school)
                .build();
    }

    @Test
    void testCreateSchoolClass_Success() {
        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

        SchoolClass result = schoolClassService.createSchoolClass(schoolClass);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("10A");
        assertThat(result.getGradeNumber()).isEqualTo(10);
        verify(schoolClassRepository, times(1)).save(schoolClass);
    }

    @Test
    void testGetSchoolClassById_Found() {
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        Optional<SchoolClass> result = schoolClassService.getSchoolClassById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("10A");
        verify(schoolClassRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSchoolClassById_NotFound() {
        when(schoolClassRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<SchoolClass> result = schoolClassService.getSchoolClassById(99L);

        assertThat(result).isEmpty();
        verify(schoolClassRepository, times(1)).findById(99L);
    }

    @Test
    void testGetAllSchoolClasses_Success() {
        when(schoolClassRepository.findAll()).thenReturn(List.of(schoolClass));

        List<SchoolClass> result = schoolClassService.getAllSchoolClasses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("10A");
        verify(schoolClassRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSchoolClasses_EmptyList() {
        when(schoolClassRepository.findAll()).thenReturn(List.of());

        List<SchoolClass> result = schoolClassService.getAllSchoolClasses();

        assertThat(result).isEmpty();
        verify(schoolClassRepository, times(1)).findAll();
    }

    @Test
    void testUpdateSchoolClass_Success() {
        SchoolClass updatedClass = SchoolClass.builder()
                .name("10B")
                .gradeNumber(11)
                .school(school)
                .build();

        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

        SchoolClass result = schoolClassService.updateSchoolClass(1L, updatedClass);

        assertThat(result).isNotNull();
        verify(schoolClassRepository, times(1)).findById(1L);
        verify(schoolClassRepository, times(1)).save(schoolClass);
    }

    @Test
    void testUpdateSchoolClass_NotFound() {
        SchoolClass updatedClass = new SchoolClass();
        when(schoolClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolClassService.updateSchoolClass(99L, updatedClass))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SchoolClassService not found");

        verify(schoolClassRepository, never()).save(any(SchoolClass.class));
    }

    @Test
    void testDeleteSchoolClass_Success() {
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        doNothing().when(schoolClassRepository).delete(schoolClass);

        schoolClassService.deleteSchoolClass(1L);

        verify(schoolClassRepository, times(1)).findById(1L);
        verify(schoolClassRepository, times(1)).delete(schoolClass);
    }

    @Test
    void testDeleteSchoolClass_NotFound() {
        when(schoolClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolClassService.deleteSchoolClass(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SchoolClassService not found");

        verify(schoolClassRepository, never()).delete(any(SchoolClass.class));
    }

    @Test
    void testGetAll_Success() {
        when(schoolClassRepository.findAll()).thenReturn(List.of(schoolClass));

        List<SchoolClass> result = schoolClassService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("10A");
        verify(schoolClassRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        when(schoolClassRepository.findAll()).thenReturn(List.of());

        List<SchoolClass> result = schoolClassService.getAll();

        assertThat(result).isEmpty();
        verify(schoolClassRepository, times(1)).findAll();
    }

    @Test
    void testGetSchoolClassesBySchool_Success() {
        when(schoolClassRepository.findBySchoolId(1L)).thenReturn(List.of(schoolClass));

        List<SchoolClass> result = schoolClassService.getSchoolClassesBySchool(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSchool().getId()).isEqualTo(1L);
        verify(schoolClassRepository, times(1)).findBySchoolId(1L);
    }

    @Test
    void testGetSchoolClassesBySchool_EmptyList() {
        when(schoolClassRepository.findBySchoolId(99L)).thenReturn(List.of());

        List<SchoolClass> result = schoolClassService.getSchoolClassesBySchool(99L);

        assertThat(result).isEmpty();
        verify(schoolClassRepository, times(1)).findBySchoolId(99L);
    }

    @Test
    void testGetSchoolClassesBySchool_MultipleClasses() {
        SchoolClass class2 = SchoolClass.builder()
                .id(2L)
                .name("10B")
                .gradeNumber(10)
                .school(school)
                .build();

        when(schoolClassRepository.findBySchoolId(1L)).thenReturn(List.of(schoolClass, class2));

        List<SchoolClass> result = schoolClassService.getSchoolClassesBySchool(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSchool().getId()).isEqualTo(1L);
        assertThat(result.get(1).getSchool().getId()).isEqualTo(1L);
        verify(schoolClassRepository, times(1)).findBySchoolId(1L);
    }

    @Test
    void testCreateSchoolClass_WithNullSchool() {
        SchoolClass classWithNullSchool = SchoolClass.builder()
                .id(2L)
                .name("11A")
                .gradeNumber(11)
                .school(null)
                .build();

        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(classWithNullSchool);

        SchoolClass result = schoolClassService.createSchoolClass(classWithNullSchool);

        assertThat(result).isNotNull();
        assertThat(result.getSchool()).isNull();
        verify(schoolClassRepository, times(1)).save(classWithNullSchool);
    }

    @Test
    void testUpdateSchoolClass_WithNullFields() {
        SchoolClass updatedClass = SchoolClass.builder()
                .name(null)
                .gradeNumber(null)
                .school(null)
                .build();

        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

        SchoolClass result = schoolClassService.updateSchoolClass(1L, updatedClass);

        assertThat(result).isNotNull();
        verify(schoolClassRepository, times(1)).save(schoolClass);
    }

    @Test
    void testCreateSchoolClass_WithDifferentGradeNumbers() {
        SchoolClass[] classes = {
                SchoolClass.builder().id(1L).name("1A").gradeNumber(1).school(school).build(),
                SchoolClass.builder().id(2L).name("5B").gradeNumber(5).school(school).build(),
                SchoolClass.builder().id(3L).name("12C").gradeNumber(12).school(school).build()
        };

        for (SchoolClass cls : classes) {
            when(schoolClassRepository.save(cls)).thenReturn(cls);
            
            SchoolClass result = schoolClassService.createSchoolClass(cls);
            
            assertThat(result).isNotNull();
            assertThat(result.getGradeNumber()).isEqualTo(cls.getGradeNumber());
        }
    }

    @Test
    void testUpdateSchoolClass_PartialUpdate() {
        SchoolClass updatedClass = SchoolClass.builder()
                .name("Updated Name")
                .gradeNumber(schoolClass.getGradeNumber()) // Keep same grade
                .school(schoolClass.getSchool()) // Keep same school
                .build();

        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

        SchoolClass result = schoolClassService.updateSchoolClass(1L, updatedClass);

        assertThat(result).isNotNull();
        verify(schoolClassRepository, times(1)).save(schoolClass);
    }
} 