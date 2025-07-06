package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @DisplayName("Save teacher and find by ID")
    void testSaveAndFindById() {
        User u = User.builder()
                .firstName("Bob")
                .lastName("Builder")
                .build();
        Teacher teacher = Teacher.builder()
                .user(u)
                .build();

        Teacher saved = teacherRepository.save(teacher);
        assertThat(saved.getId()).isNotNull();

        Optional<Teacher> found = teacherRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getFirstName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Find all teachers")
    void testFindAll() {
        User u1 = User.builder()
                .firstName("Bob")
                .lastName("Builder")
                .build();
        User u2 = User.builder()
                .firstName("Bob")
                .lastName("Builder")
                .build();
        Teacher t1 = Teacher.builder().user(u1).build();
        Teacher t2 = Teacher.builder().user(u2).build();
        teacherRepository.saveAll(List.of(t1, t2));

        List<Teacher> teachers = teacherRepository.findAll();
        assertThat(teachers).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete teacher")
    void testDelete() {
        User u = User.builder()
                .firstName("Bob")
                .lastName("Builder")
                .build();
        Teacher teacher = Teacher.builder().user(u).build();
        teacher = teacherRepository.save(teacher);
        Long id = teacher.getId();

        teacherRepository.delete(teacher);
        Optional<Teacher> deleted = teacherRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}