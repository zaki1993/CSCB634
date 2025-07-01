package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.repository.StudentRepository;
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

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStudent_Success() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Alice");

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student created = studentService.createStudent(student);
        assertThat(created).isNotNull();
        assertThat(created.getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testGetStudentById_Found() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Optional<Student> result = studentService.getStudentById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.getStudentById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllStudents() {
        Student s1 = new Student();
        Student s2 = new Student();
        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Student> students = studentService.getAllStudents();
        assertThat(students).hasSize(2);
    }

    @Test
    void testUpdateStudent_Success() {
        Student existing = new Student();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("Name");

        Student update = new Student();
        update.setFirstName("New");
        update.setLastName("NameNew");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(existing);

        Student updated = studentService.updateStudent(1L, update);
        assertThat(updated.getFirstName()).isEqualTo("New");
        assertThat(updated.getLastName()).isEqualTo("NameNew");
    }

    @Test
    void testUpdateStudent_NotFound() {
        Student update = new Student();
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(99L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void testDeleteStudent_Success() {
        Student existing = new Student();
        existing.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(studentRepository).delete(existing);

        studentService.deleteStudent(1L);
        verify(studentRepository).delete(existing);
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }
}