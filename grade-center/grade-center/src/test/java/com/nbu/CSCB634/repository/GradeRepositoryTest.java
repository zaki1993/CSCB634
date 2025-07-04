package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Subject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Save grade and find by ID")
    void testSaveAndFindById() {
        Student student = Student.builder().firstName("Test").lastName("Student").build();
        student = studentRepository.save(student);

        Subject subject = Subject.builder().name("Math").build();

        Grade grade = Grade.builder()
                .student(student)
                .subject(subject)
                .value(5)
                .dateAwarded(LocalDate.now())
                .build();

        Grade saved = gradeRepository.save(grade);
        assertThat(saved.getId()).isNotNull();

        var found = gradeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("Find grades by student")
    void testFindByStudent() {
        Student student = Student.builder().firstName("Find").lastName("Me").build();
        student = studentRepository.save(student);

        Grade grade1 = Grade.builder()
                .student(student)
                .subject(Subject.builder().name("Science").build())
                .value(4)
                .dateAwarded(LocalDate.now())
                .build();

        Grade grade2 = Grade.builder()
                .student(student)
                .subject(Subject.builder().name("History").build())
                .value(3)
                .dateAwarded(LocalDate.now())
                .build();

        gradeRepository.saveAll(List.of(grade1, grade2));

        List<Grade> grades = gradeRepository.findByStudent(student);
        assertThat(grades).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete grade")
    void testDelete() {
        Student student = Student.builder().firstName("Del").lastName("Grade").build();
        student = studentRepository.save(student);

        Grade grade = Grade.builder()
                .student(student)
                .subject(Subject.builder().name("Chemistry").build())
                .value(4)
                .dateAwarded(LocalDate.now())
                .build();

        grade = gradeRepository.save(grade);
        Long id = grade.getId();

        gradeRepository.delete(grade);
        var found = gradeRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Find by student returns empty list if none")
    void testFindByStudentNoGrades() {
        Student student = Student.builder().firstName("NoGrades").lastName("Here").build();
        student = studentRepository.save(student);

        List<Grade> grades = gradeRepository.findByStudent(student);
        assertThat(grades).isEmpty();
    }
}