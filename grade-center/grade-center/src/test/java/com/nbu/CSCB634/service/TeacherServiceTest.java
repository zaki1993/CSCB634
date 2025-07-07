package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.TeacherRepository;
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

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTeacher_Success() {
        User user = User.builder().firstName("Bob").lastName("Smith").build();
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUser(user);

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher created = teacherService.createTeacher(teacher);
        assertThat(created).isNotNull();
        assertThat(created.getUser().getFirstName()).isEqualTo("Bob");
        assertThat(created.getUser().getLastName()).isEqualTo("Smith");
    }

    @Test
    void testGetTeacherById_Found() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Optional<Teacher> result = teacherService.getTeacherById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void testGetTeacherById_NotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Teacher> result = teacherService.getTeacherById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllTeachers() {
        Teacher t1 = new Teacher();
        Teacher t2 = new Teacher();
        when(teacherRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Teacher> teachers = teacherService.getAllTeachers();
        assertThat(teachers).hasSize(2);
    }

    @Test
    void testUpdateTeacher_NotFound() {
        Teacher update = new Teacher();
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.updateTeacher(99L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    void testDeleteTeacher_Success() {
        Teacher existing = new Teacher();
        existing.setId(1L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(teacherRepository).delete(existing);

        teacherService.deleteTeacher(1L);
        verify(teacherRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteTeacher_NotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.deleteTeacher(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Teacher not found");
    }
}
