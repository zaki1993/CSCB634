package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

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

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

        Optional<School> result = schoolService.getSchoolById(1L);
        assertThat(result).isPresent();
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
        existing.setName("Old");

        School update = new School();
        update.setName("New");

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(schoolRepository.save(any(School.class))).thenReturn(existing);

        School updated = schoolService.updateSchool(1L, update);
        assertThat(updated.getName()).isEqualTo("New");
    }

    @Test
    void testUpdateSchool_NotFound() {
        School update = new School();
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolService.updateSchool(99L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School not found");
    }

    @Test
    void testDeleteSchool_Success() {
        School existing = new School();
        existing.setId(1L);

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(schoolRepository).delete(existing);

        schoolService.deleteSchool(1L);
        verify(schoolRepository).delete(existing);
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
        School s2 = new School();

        when(schoolRepository.findAll()).thenReturn(List.of(s1, s2));
        var schools = schoolService.getAllSchools();
        assertThat(schools).hasSize(2);
    }
}