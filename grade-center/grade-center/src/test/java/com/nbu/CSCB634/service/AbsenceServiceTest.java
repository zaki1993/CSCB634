package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.repository.AbsenceRepository;
import com.nbu.CSCB634.repository.ParentRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import com.nbu.CSCB634.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbsenceServiceTest {

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ParentRepository parentRepository;

    @InjectMocks
    private AbsenceService absenceService;

    private Student student;
    private Teacher teacher;
    private Parent parent;
    private Absence absence;
    private School school;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        school = School.builder()
                .id(1L)
                .name("Test School")
                .build();

        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .school(school)
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .school(school)
                .build();

        parent = Parent.builder()
                .id(1L)
                .students(List.of(student))
                .build();

        absence = Absence.builder()
                .id(1L)
                .student(student)
                .teacher(teacher)
                .absenceDate(LocalDate.now())
                .justified(false)
                .build();
    }

    @Test
    void testCreateAbsence_Success() {
        when(absenceRepository.save(any(Absence.class))).thenReturn(absence);

        Absence result = absenceService.createAbsence(absence);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(absenceRepository, times(1)).save(absence);
    }

    @Test
    void testGetAbsenceById_Found() {
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));

        Optional<Absence> result = absenceService.getAbsenceById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void testGetAbsenceById_NotFound() {
        when(absenceRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Absence> result = absenceService.getAbsenceById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetAbsencesByStudentId_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(absenceRepository.findByStudent(student)).thenReturn(List.of(absence));

        List<Absence> result = absenceService.getAbsencesByStudentId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudent().getId()).isEqualTo(1L);
    }

    @Test
    void testGetAbsencesByStudentId_StudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getAbsencesByStudentId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void testGetAbsencesByTeacherId_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(absenceRepository.findByTeacher(teacher)).thenReturn(List.of(absence));

        List<Absence> result = absenceService.getAbsencesByTeacherId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeacher().getId()).isEqualTo(1L);
    }

    @Test
    void testGetAbsencesByTeacherId_TeacherNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getAbsencesByTeacherId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    void testUpdateAbsence_Success() {
        Absence updatedAbsence = Absence.builder()
                .absenceDate(LocalDate.now().plusDays(1))
                .justified(true)
                .build();

        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(absence);

        Absence result = absenceService.updateAbsence(1L, updatedAbsence);

        assertThat(result).isNotNull();
        verify(absenceRepository, times(1)).save(absence);
    }

    @Test
    void testUpdateAbsence_NotFound() {
        Absence updatedAbsence = new Absence();
        when(absenceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.updateAbsence(99L, updatedAbsence))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Absence not found");
    }

    @Test
    void testDeleteAbsence_Success() {
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));
        doNothing().when(absenceRepository).delete(absence);

        absenceService.deleteAbsence(1L);

        verify(absenceRepository, times(1)).delete(absence);
    }

    @Test
    void testDeleteAbsence_NotFound() {
        when(absenceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.deleteAbsence(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Absence not found");
    }

    @Test
    void testGetAll_Success() {
        when(absenceRepository.findAll()).thenReturn(List.of(absence));

        List<Absence> result = absenceService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetStudentsForTeacher_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findBySchoolId(1L)).thenReturn(List.of(student));

        List<Student> result = absenceService.getStudentsForTeacher(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetStudentsForTeacher_TeacherNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getStudentsForTeacher(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    void testCanTeacherManageStudent_SameSchool() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        boolean result = absenceService.canTeacherManageStudent(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void testCanTeacherManageStudent_DifferentSchool() {
        Student differentSchoolStudent = Student.builder()
                .id(2L)
                .school(School.builder().id(2L).name("Other School").build())
                .build();

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(differentSchoolStudent));

        boolean result = absenceService.canTeacherManageStudent(1L, 2L);

        assertThat(result).isFalse();
    }

    @Test
    void testCanTeacherManageStudent_TeacherNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.canTeacherManageStudent(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    void testCanTeacherManageStudent_StudentNotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.canTeacherManageStudent(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void testGetAbsencesByParentId_Success() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(absenceRepository.findByStudent(student)).thenReturn(List.of(absence));

        List<Absence> result = absenceService.getAbsencesByParentId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetAbsencesByParentId_ParentNotFound() {
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getAbsencesByParentId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parent not found");
    }

    @Test
    void testGetAbsencesByParentId_NoStudents() {
        Parent parentWithNoStudents = Parent.builder()
                .id(2L)
                .students(List.of())
                .build();

        when(parentRepository.findById(2L)).thenReturn(Optional.of(parentWithNoStudents));

        List<Absence> result = absenceService.getAbsencesByParentId(2L);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetAbsencesByParentAndStudent_Success() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(absenceRepository.findByStudent(student)).thenReturn(List.of(absence));

        List<Absence> result = absenceService.getAbsencesByParentAndStudent(1L, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetAbsencesByParentAndStudent_ParentNotFound() {
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getAbsencesByParentAndStudent(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parent not found");
    }

    @Test
    void testGetAbsencesByParentAndStudent_StudentNotFound() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getAbsencesByParentAndStudent(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void testGetAbsencesByParentAndStudent_StudentNotBelongToParent() {
        Student otherStudent = Student.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(otherStudent));

        assertThatThrownBy(() -> absenceService.getAbsencesByParentAndStudent(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student does not belong to this parent");
    }

    @Test
    void testCanParentViewStudent_Success() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        boolean result = absenceService.canParentViewStudent(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void testCanParentViewStudent_NotParentsChild() {
        Student otherStudent = Student.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(otherStudent));

        boolean result = absenceService.canParentViewStudent(1L, 2L);

        assertThat(result).isFalse();
    }

    @Test
    void testGetStudentsForParent_Success() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));

        List<Student> result = absenceService.getStudentsForParent(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetStudentsForParent_ParentNotFound() {
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> absenceService.getStudentsForParent(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parent not found");
    }

    @Test
    void testGetStudentsForParent_NoStudents() {
        Parent parentWithNoStudents = Parent.builder()
                .id(2L)
                .students(null)
                .build();

        when(parentRepository.findById(2L)).thenReturn(Optional.of(parentWithNoStudents));

        List<Student> result = absenceService.getStudentsForParent(2L);

        assertThat(result).isEmpty();
    }
} 