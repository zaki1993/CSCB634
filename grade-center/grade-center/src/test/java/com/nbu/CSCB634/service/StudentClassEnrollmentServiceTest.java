package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.SchoolClassRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentClassEnrollmentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @InjectMocks
    private StudentClassEnrollmentService enrollmentService;

    private Student student;
    private SchoolClass schoolClass;
    private School school;
    private User user;

    @BeforeEach
    void setUp() {
        // Създаваме тестови данни
        school = School.builder()
                .id(1L)
                .name("Тестово училище")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Тест")
                .lastName("Потребител")
                .build();

        student = Student.builder()
                .id(1L)
                .user(user)
                .school(school)
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .name("5А")
                .gradeNumber(5)
                .school(school)
                .students(new ArrayList<>())
                .build();
    }

    @Test
    void enrollStudentInClass_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student result = enrollmentService.enrollStudentInClass(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(schoolClass, result.getSchoolClass());
        verify(studentRepository).save(student);
    }

    @Test
    void enrollStudentInClass_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> enrollmentService.enrollStudentInClass(1L, 1L));
    }

    @Test
    void enrollStudentInClass_ClassNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> enrollmentService.enrollStudentInClass(1L, 1L));
    }

    @Test
    void enrollStudentInClass_DifferentSchools() {
        // Arrange
        School differentSchool = School.builder()
                .id(2L)
                .name("Друго училище")
                .build();
        
        SchoolClass differentSchoolClass = SchoolClass.builder()
                .id(2L)
                .name("6Б")
                .school(differentSchool)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(2L)).thenReturn(Optional.of(differentSchoolClass));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> enrollmentService.enrollStudentInClass(1L, 2L));
    }

    @Test
    void enrollStudentInClass_AlreadyEnrolled() {
        // Arrange
        student.setSchoolClass(schoolClass);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> enrollmentService.enrollStudentInClass(1L, 1L));
    }

    @Test
    void withdrawStudentFromClass_Success() {
        // Arrange
        student.setSchoolClass(schoolClass);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student result = enrollmentService.withdrawStudentFromClass(1L);

        // Assert
        assertNotNull(result);
        assertNull(result.getSchoolClass());
        verify(studentRepository).save(student);
    }

    @Test
    void withdrawStudentFromClass_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> enrollmentService.withdrawStudentFromClass(1L));
    }

    @Test
    void withdrawStudentFromClass_NotEnrolled() {
        // Arrange
        student.setSchoolClass(null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> enrollmentService.withdrawStudentFromClass(1L));
    }

    @Test
    void transferStudentToClass_Success() {
        // Arrange
        SchoolClass oldClass = SchoolClass.builder()
                .id(2L)
                .name("5Б")
                .school(school)
                .build();
        
        student.setSchoolClass(oldClass);
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student result = enrollmentService.transferStudentToClass(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(schoolClass, result.getSchoolClass());
        verify(studentRepository).save(student);
    }

    @Test
    void getStudentsInClass_Success() {
        // Arrange
        student.setSchoolClass(schoolClass);
        List<Student> allStudents = List.of(student);
        
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.findAll()).thenReturn(allStudents);

        // Act
        List<Student> result = enrollmentService.getStudentsInClass(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
    }

    @Test
    void getStudentsWithoutClass_Success() {
        // Arrange
        student.setSchoolClass(null);
        List<Student> allStudents = List.of(student);
        
        when(studentRepository.findAll()).thenReturn(allStudents);

        // Act
        List<Student> result = enrollmentService.getStudentsWithoutClass(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
    }

    @Test
    void canEnrollStudentInClass_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // Act
        boolean result = enrollmentService.canEnrollStudentInClass(1L, 1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void canEnrollStudentInClass_AlreadyEnrolled() {
        // Arrange
        student.setSchoolClass(schoolClass);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));

        // Act
        boolean result = enrollmentService.canEnrollStudentInClass(1L, 1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void canEnrollStudentInClass_DifferentSchools() {
        // Arrange
        School differentSchool = School.builder()
                .id(2L)
                .name("Друго училище")
                .build();
        
        SchoolClass differentSchoolClass = SchoolClass.builder()
                .id(2L)
                .name("6Б")
                .school(differentSchool)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(2L)).thenReturn(Optional.of(differentSchoolClass));

        // Act
        boolean result = enrollmentService.canEnrollStudentInClass(1L, 2L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getStudentCountInClass_Success() {
        // Arrange
        student.setSchoolClass(schoolClass);
        List<Student> allStudents = List.of(student);
        
        when(schoolClassRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.findAll()).thenReturn(allStudents);

        // Act
        long result = enrollmentService.getStudentCountInClass(1L);

        // Assert
        assertEquals(1, result);
    }
} 