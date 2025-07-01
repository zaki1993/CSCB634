package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.school.electronicdiary.model.School;
import com.school.electronicdiary.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchoolServiceTest {

    @InjectMocks
    private SchoolService schoolService;

    @Mock
    private SchoolRepository schoolRepository;

    private School school;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("123 Street")
                .build();
    }

    @Test
    void createSchoolSuccess() {
        when(schoolRepository.save(any(School.class))).thenReturn(school);

        School created = schoolService.createSchool(school);

        assertEquals("Test School", created.getName());
        assertEquals("123 Street", created.getAddress());
    }

    @Test
    void getSchoolByIdSuccess() {
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

        Optional<School> found = schoolService.getSchoolById(1L);

        assertTrue(found.isPresent());
        assertEquals("Test School", found.get().getName());
    }

    @Test
    void updateSchoolSuccess() {
        School newDetails = School.builder()
                .name("New School Name")
                .address("New Address")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(schoolRepository.save(any(School.class))).thenReturn(newDetails);

        School updated = schoolService.updateSchool(1L, newDetails);

        assertEquals("New School Name", updated.getName());
        assertEquals("New Address", updated.getAddress());
    }

    @Test
    void updateSchoolFailsWhenNotFound() {
        when(schoolRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> schoolService.updateSchool(2L, school));

        assertEquals("School not found", thrown.getMessage());
    }

    @Test
    void deleteSchoolSuccess() {
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        doNothing().when(schoolRepository).delete(school);

        assertDoesNotThrow(() -> schoolService.deleteSchool(1L));
        verify(schoolRepository, times(1)).delete(school);
    }

    @Test
    void deleteSchoolFailsWhenNotFound() {
        when(schoolRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> schoolService.deleteSchool(2L));

        assertEquals("School not found", thrown.getMessage());
    }
}