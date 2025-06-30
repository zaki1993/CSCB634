package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.TeacherRepository;
import com.nbu.CSCB634.repository.diploma.defense.DiplomaDefenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private DiplomaDefenseRepository diplomaDefenseRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void getAllTeachers() {
        List<Teacher> mockTeachers = List.of(new Teacher(), new Teacher());
        when(teacherRepository.findAll()).thenReturn(mockTeachers);

        List<Teacher> teachers = teacherService.getAllTeachers();

        assertEquals(2, teachers.size());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void getTeacherById_Found() {
        Teacher mockTeacher = new Teacher();
        mockTeacher.setId(1L);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(mockTeacher));

        Teacher teacher = teacherService.getTeacherById(1L);

        assertNotNull(teacher);
        assertEquals(1L, teacher.getId());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void getTeacherById_NotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        Teacher teacher = teacherService.getTeacherById(1L);

        assertNull(teacher);
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void saveTeacher() {
        Teacher mockTeacher = new Teacher();
        when(teacherRepository.save(mockTeacher)).thenReturn(mockTeacher);

        Teacher savedTeacher = teacherService.saveTeacher(mockTeacher);

        assertNotNull(savedTeacher);
        verify(teacherRepository, times(1)).save(mockTeacher);
    }

    @Test
    void deleteTeacher() {
        teacherService.deleteTeacher(1L);

        verify(teacherRepository, times(1)).deleteById(1L);
    }

    @Test
    void getTeacherByUsername() {
        Teacher mockTeacher = new Teacher();
        mockTeacher.setUsername("john_doe");
        when(teacherRepository.findByUsername("john_doe")).thenReturn(mockTeacher);

        Teacher teacher = teacherService.getTeacherByUsername("john_doe");

        assertNotNull(teacher);
        assertEquals("john_doe", teacher.getUsername());
        verify(teacherRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void getSuccessfulDefensesByTeacher() {
        when(diplomaDefenseRepository.countSuccessfulDefensesByTeacher(1L)).thenReturn(5L);

        long successfulDefenses = teacherService.getSuccessfulDefensesByTeacher(1L);

        assertEquals(5L, successfulDefenses);
        verify(diplomaDefenseRepository, times(1)).countSuccessfulDefensesByTeacher(1L);
    }
}
