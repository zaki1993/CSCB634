package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.School;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SchoolRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    @DisplayName("Save school and find by ID")
    void testSaveAndFindById() {
        School school = School.builder()
                .name("Test School")
                .address("123 Street")
                .build();

        School saved = schoolRepository.save(school);
        assertThat(saved.getId()).isNotNull();

        Optional<School> found = schoolRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test School");
    }

    @Test
    @DisplayName("Find all schools")
    void testFindAll() {
        School s1 = School.builder().name("Alpha School").build();
        School s2 = School.builder().name("Beta School").build();
        schoolRepository.saveAll(List.of(s1, s2));

        List<School> schools = schoolRepository.findAll();
        assertThat(schools).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete school")
    void testDelete() {
        School school = School.builder().name("Delete Me").build();
        school = schoolRepository.save(school);
        Long id = school.getId();

        schoolRepository.delete(school);
        Optional<School> deleted = schoolRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}