package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    @DisplayName("Save student and find by ID")
    void testSaveAndFindById() {
        Student student = Student.builder()
                .user(user)
                .build();

        Student saved = studentRepository.save(student);
        assertThat(saved.getId()).isNotNull();

        Optional<Student> found = studentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Find all students")
    void testFindAll() {
        Student s1 = Student.builder().user(user).build();
        Student s2 = Student.builder().user(user).build();
        studentRepository.saveAll(List.of(s1, s2));

        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete student")
    void testDelete() {
        Student student = Student.builder().user(user).build();
        student = studentRepository.save(student);
        Long id = student.getId();

        studentRepository.delete(student);
        Optional<Student> deleted = studentRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}