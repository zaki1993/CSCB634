package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    private Student student;
    private School school;
    private SchoolClass schoolClass;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        school = School.builder()
                .id(1L)
                .name("Demo School")
                .address("Some Address")
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .name("1A")
                .gradeNumber(1)
                .school(school)
                .build();

        student = Student.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Wonderland")
                .school(school)
                .schoolClass(schoolClass)
                .build();
    }

    @Test
    void createStudentSuccess() {
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student created = studentService.createStudent(student);

        assertEquals("Alice", created.getFirstName());
        assertEquals("Wonderland", created.getLastName());
    }

    @Test
    void getStudentByIdSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Optional<Student> found = studentService.getStudentById(1L);

        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getFirstName());
    }

    @Test
    void updateStudentSuccess() {
        Student newDetails = Student.builder()
                .firstName("Bob")
                .lastName("Builder")
                .school(school)
                .schoolClass(schoolClass)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(newDetails);

        Student updated = studentService.updateStudent(1L, newDetails);

        assertEquals("Bob", updated.getFirstName());
        assertEquals("Builder", updated.getLastName());
    }

    @Test
    void updateStudentFailsWhenNotFound() {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(2L, student));

        assertEquals("Student not found", thrown.getMessage());
    }

    @Test
    void deleteStudentSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(student);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    void deleteStudentFailsWhenNotFound() {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> studentService.deleteStudent(2L));

        assertEquals("Student not found", thrown.getMessage());
    }
}