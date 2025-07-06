package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.repository.SchoolRepository;
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

class SchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private SchoolService schoolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSchool_Success() {
        School school = new School();
        school.setId(1L);
        school.setName("Test School");

        when(schoolRepository.save(any(School.class))).thenReturn(school);

        School created = schoolService.createSchool(school);
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Test School");
    }

    @Test
    void testGetSchoolById_Found() {
        School school = new School();
        school.setId(1L);
        school.setName("Example School");

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

        Optional<School> result = schoolService.getSchoolById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Example School");
    }

    @Test
    void testGetSchoolById_NotFound() {
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<School> result = schoolService.getSchoolById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateSchool_Success() {
        School existing = new School();
        existing.setId(1L);
        existing.setName("Old Name");

        School update = new School();
        update.setName("New Name");

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(schoolRepository.save(any(School.class))).thenReturn(existing);

        School updated = schoolService.updateSchool(1L, update);

        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void testUpdateSchool_NotFound() {
        School update = new School();
        update.setName("New Name");

        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolService.updateSchool(99L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School not found");
    }

    @Test
    void testDeleteSchool_Success() {
        School existing = new School();
        existing.setId(1L);
        existing.setName("Test School");

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(schoolRepository).delete(existing);

        schoolService.deleteSchool(1L);

        verify(schoolRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteSchool_NotFound() {
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolService.deleteSchool(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School not found");
    }

    @Test
    void testGetAllSchools() {
        School s1 = new School();
        s1.setId(1L);
        s1.setName("School A");

        School s2 = new School();
        s2.setId(2L);
        s2.setName("School B");

        when(schoolRepository.findAll()).thenReturn(List.of(s1, s2));

        List<School> schools = schoolService.getAllSchools();

        assertThat(schools).hasSize(2);
        assertThat(schools.get(0).getName()).isEqualTo("School A");
        assertThat(schools.get(1).getName()).isEqualTo("School B");
    }
}
