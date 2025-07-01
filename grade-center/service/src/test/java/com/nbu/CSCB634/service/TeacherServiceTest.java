package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.TeacherRepository;
import com.school.electronicdiary.model.School;
import com.school.electronicdiary.model.Teacher;
import com.school.electronicdiary.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    private Teacher teacher;
    private School school;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("Test Address")
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .qualifiedSubjects(Set.of("Mathematics", "Physics"))
                .school(school)
                .build();
    }

    @Test
    void createTeacherSuccess() {
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher created = teacherService.createTeacher(teacher);

        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertTrue(created.getQualifiedSubjects().contains("Mathematics"));
    }

    @Test
    void getTeacherByIdSuccess() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Optional<Teacher> optTeacher = teacherService.getTeacherById(1L);

        assertTrue(optTeacher.isPresent());
        assertEquals("John", optTeacher.get().getFirstName());
    }

    @Test
    void updateTeacherSuccess() {
        Teacher newDetails = Teacher.builder()
                .firstName("Jane")
                .lastName("Smith")
                .qualifiedSubjects(Set.of("Biology"))
                .school(school)
                .build();

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(newDetails);

        Teacher updated = teacherService.updateTeacher(1L, newDetails);

        assertEquals("Jane", updated.getFirstName());
        assertTrue(updated.getQualifiedSubjects().contains("Biology"));
    }

    @Test
    void updateTeacherFailsWhenNotFound() {
        when(teacherRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> teacherService.updateTeacher(2L, teacher));

        assertEquals("Teacher not found", thrown.getMessage());
    }

    @Test
    void deleteTeacherSuccess() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        doNothing().when(teacherRepository).delete(teacher);

        assertDoesNotThrow(() -> teacherService.deleteTeacher(1L));
        verify(teacherRepository, times(1)).delete(teacher);
    }

    @Test
    void deleteTeacherFailsWhenNotFound() {
        when(teacherRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> teacherService.deleteTeacher(2L));

        assertEquals("Teacher not found", thrown.getMessage());
    }
}