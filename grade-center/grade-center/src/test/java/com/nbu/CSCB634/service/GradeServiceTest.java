package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.repository.GradeRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGrade_Success() {
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setValue(5);

        when(gradeRepository.save(any(Grade.class))).thenReturn(grade);

        Grade savedGrade = gradeService.createGrade(grade);
        assertThat(savedGrade).isNotNull();
        assertThat(savedGrade.getValue()).isEqualTo(5);
        verify(gradeRepository).save(grade);
    }

    @Test
    void testGetGradesByStudentId_Found() {
        Student student = new Student();
        student.setId(1L);

        Grade grade1 = new Grade();
        grade1.setId(1L);
        grade1.setStudent(student);
        Grade grade2 = new Grade();
        grade2.setId(2L);
        grade2.setStudent(student);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(gradeRepository.findByStudent(student)).thenReturn(List.of(grade1, grade2));

        List<Grade> result = gradeService.getGradesByStudentId(1L);
        assertThat(result).hasSize(2);
    }

    @Test
    void testGetGradesByStudentId_StudentNotFound_Throws() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.getGradesByStudentId(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void testGetGradeById_Found() {
        Grade grade = new Grade();
        grade.setId(1L);
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        Optional<Grade> result = gradeService.getGradeById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void testGetGradeById_NotFound() {
        when(gradeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Grade> result = gradeService.getGradeById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateGrade_Success() {
        Grade existing = new Grade();
        existing.setId(1L);
        existing.setValue(4);
        existing.setDateAwarded(LocalDate.now().minusDays(1));
        Subject oldSubject = Subject.builder().id(1L).name("Math").build();
        existing.setSubject(oldSubject);

        Grade updatedInfo = new Grade();
        updatedInfo.setValue(5);
        updatedInfo.setDateAwarded(LocalDate.now());
        Subject newSubject = Subject.builder().id(2L).name("Physics").build();
        updatedInfo.setSubject(newSubject);

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(gradeRepository.save(any(Grade.class))).thenReturn(existing);

        Grade updated = gradeService.updateGrade(1L, updatedInfo);
        assertThat(updated.getValue()).isEqualTo(5);
        assertThat(updated.getDateAwarded()).isEqualTo(LocalDate.now());
        assertThat(updated.getSubject()).isEqualTo(newSubject);
    }

    @Test
    void testUpdateGrade_NotFound() {
        Grade updatedInfo = new Grade();
        when(gradeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.updateGrade(99L, updatedInfo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grade not found");
    }

    @Test
    void testDeleteGrade_Success() {
        Grade existing = new Grade();
        existing.setId(1L);

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(gradeRepository).delete(existing);

        gradeService.deleteGrade(1L);
        verify(gradeRepository).delete(existing);
    }

    @Test
    void testDeleteGrade_NotFound() {
        when(gradeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.deleteGrade(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grade not found");
    }
}