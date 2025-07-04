package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Teacher;
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
        Teacher teacher = Teacher.builder()
                .firstName("Bob")
                .lastName("Builder")
                .build();

        Teacher saved = teacherRepository.save(teacher);
        assertThat(saved.getId()).isNotNull();

        Optional<Teacher> found = teacherRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Find all teachers")
    void testFindAll() {
        Teacher t1 = Teacher.builder().firstName("Ann").lastName("Smith").build();
        Teacher t2 = Teacher.builder().firstName("Joe").lastName("Black").build();
        teacherRepository.saveAll(List.of(t1, t2));

        List<Teacher> teachers = teacherRepository.findAll();
        assertThat(teachers).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete teacher")
    void testDelete() {
        Teacher teacher = Teacher.builder().firstName("Delete").lastName("Me").build();
        teacher = teacherRepository.save(teacher);
        Long id = teacher.getId();

        teacherRepository.delete(teacher);
        Optional<Teacher> deleted = teacherRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}