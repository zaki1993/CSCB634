package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
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
        User u = User.builder().firstName("Alice").lastName("Smith").build();
        Student student = new Student();
        student.setId(1L);
        student.setUser(u);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student created = studentService.createStudent(student);
        assertThat(created).isNotNull();
        assertThat(created.getUser().getFirstName()).isEqualTo("Alice");
        assertThat(created.getUser().getLastName()).isEqualTo("Smith");
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
        verify(studentRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }
}
