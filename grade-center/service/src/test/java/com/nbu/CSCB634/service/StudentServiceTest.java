package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.repository.StudentRepository;
import com.nbu.CSCB634.service.exceptions.StudentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getAllStudents() {
        List<Student> mockStudents = List.of(new Student(), new Student());
        when(studentRepository.findAll()).thenReturn(mockStudents);

        List<Student> students = studentService.getAllStudents();

        assertEquals(2, students.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentById_Found() {
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));

        Student student = studentService.getStudentById(1L);

        assertNotNull(student);
        assertEquals(1L, student.getId());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentById_NotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        Student student = studentService.getStudentById(1L);

        assertNull(student);
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentByFn_Found() {
        Student mockStudent = new Student();
        mockStudent.setFn("12345");
        when(studentRepository.findByFn("12345")).thenReturn(Optional.of(mockStudent));

        Student student = studentService.getStudentByFn("12345");

        assertNotNull(student);
        assertEquals("12345", student.getFn());
        verify(studentRepository, times(1)).findByFn("12345");
    }

    @Test
    void getStudentByFn_NotFound() {
        when(studentRepository.findByFn("12345")).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentByFn("12345"));
        verify(studentRepository, times(1)).findByFn("12345");
    }

    @Test
    void getGraduatedStudentsInPeriod() {
        List<Student> mockStudents = List.of(new Student(), new Student());
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();
        when(studentRepository.findGraduatedStudentsInPeriod(startDate, endDate)).thenReturn(mockStudents);

        List<Student> students = studentService.getGraduatedStudentsInPeriod(startDate, endDate);

        assertEquals(2, students.size());
        verify(studentRepository, times(1)).findGraduatedStudentsInPeriod(startDate, endDate);
    }
}
