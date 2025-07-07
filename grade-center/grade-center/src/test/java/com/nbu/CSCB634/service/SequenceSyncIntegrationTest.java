package com.nbu.CSCB634.service;

import com.nbu.CSCB634.config.DatabaseSequenceSync;
import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.*;
import com.nbu.CSCB634.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SequenceSyncIntegrationTest {

    @Autowired
    private GradeService gradeService;
    
    @Autowired
    private AbsenceService absenceService;
    
    @Autowired
    private DatabaseSequenceSync sequenceSync;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;

    private Student student;
    private Teacher teacher;
    private Subject subject;
    private School school;

    @BeforeEach
    void setUp() {
        // Създаваме тестови данни
        school = School.builder()
                .name("Test School")
                .address("Test Address")
                .build();
        school = schoolRepository.save(school);

        subject = Subject.builder()
                .name("Test Subject")
                .build();
        subject = subjectRepository.save(subject);

        User teacherUser = User.builder()
                .username("testteacher")
                .firstName("Test")
                .lastName("Teacher")
                .email("teacher@test.com")
                .password("password")
                .role(Role.TEACHER)
                .build();
        teacherUser = userRepository.save(teacherUser);

        teacher = Teacher.builder()
                .id(teacherUser.getId())
                .user(teacherUser)
                .school(school)
                .build();
        teacher = teacherRepository.save(teacher);

        User studentUser = User.builder()
                .username("teststudent")
                .firstName("Test")
                .lastName("Student")
                .email("student@test.com")
                .password("password")
                .role(Role.STUDENT)
                .build();
        studentUser = userRepository.save(studentUser);

        student = Student.builder()
                .id(studentUser.getId())
                .user(studentUser)
                .school(school)
                .build();
        student = studentRepository.save(student);
    }

    @Test
    void testGradeCreationWithSequenceSync() {
        // Arrange
        Grade grade = Grade.builder()
                .value(85)
                .dateAwarded(LocalDate.now())
                .student(student)
                .teacher(teacher)
                .subject(subject)
                .build();

        // Act
        Grade savedGrade = gradeService.createGrade(grade);

        // Assert
        assertNotNull(savedGrade);
        assertNotNull(savedGrade.getId());
        assertEquals(85, savedGrade.getValue());
        assertEquals(student.getId(), savedGrade.getStudent().getId());
        assertEquals(teacher.getId(), savedGrade.getTeacher().getId());
        assertEquals(subject.getId(), savedGrade.getSubject().getId());
    }

    @Test
    void testAbsenceCreationWithSequenceSync() {
        // Arrange
        Absence absence = Absence.builder()
                .absenceDate(LocalDate.now())
                .justified(false)
                .student(student)
                .teacher(teacher)
                .subject(subject)
                .build();

        // Act
        Absence savedAbsence = absenceService.createAbsence(absence);

        // Assert
        assertNotNull(savedAbsence);
        assertNotNull(savedAbsence.getId());
        assertEquals(LocalDate.now(), savedAbsence.getAbsenceDate());
        assertEquals(false, savedAbsence.getJustified());
        assertEquals(student.getId(), savedAbsence.getStudent().getId());
        assertEquals(teacher.getId(), savedAbsence.getTeacher().getId());
        assertEquals(subject.getId(), savedAbsence.getSubject().getId());
    }

    @Test
    void testMultipleGradeCreation() {
        // Тест за създаване на множество оценки последователно
        for (int i = 1; i <= 5; i++) {
            Grade grade = Grade.builder()
                    .value(80 + i)
                    .dateAwarded(LocalDate.now())
                    .student(student)
                    .teacher(teacher)
                    .subject(subject)
                    .build();

            Grade savedGrade = gradeService.createGrade(grade);
            assertNotNull(savedGrade);
            assertNotNull(savedGrade.getId());
            assertEquals(80 + i, savedGrade.getValue());
        }
    }

    @Test
    void testMultipleAbsenceCreation() {
        // Тест за създаване на множество отсъствия последователно
        for (int i = 0; i < 5; i++) {
            Absence absence = Absence.builder()
                    .absenceDate(LocalDate.now().minusDays(i))
                    .justified(i % 2 == 0)
                    .student(student)
                    .teacher(teacher)
                    .subject(subject)
                    .build();

            Absence savedAbsence = absenceService.createAbsence(absence);
            assertNotNull(savedAbsence);
            assertNotNull(savedAbsence.getId());
            assertEquals(LocalDate.now().minusDays(i), savedAbsence.getAbsenceDate());
            assertEquals(i % 2 == 0, savedAbsence.getJustified());
        }
    }

    @Test
    void testSequenceSyncMethods() {
        // Тест за директно извикване на sequence sync методите
        assertDoesNotThrow(() -> {
            sequenceSync.syncGradeSequence();
            sequenceSync.syncAbsenceSequence();
            sequenceSync.syncAllSequences();
        });
    }
} 