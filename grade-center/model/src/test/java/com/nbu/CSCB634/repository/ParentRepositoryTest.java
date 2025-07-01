package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Parent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    @Test
    @DisplayName("Save parent and find by ID")
    void testSaveAndFindById() {
        Parent parent = Parent.builder()
                .firstName("Mary")
                .lastName("Poppins")
                .build();

        Parent saved = parentRepository.save(parent);
        assertThat(saved.getId()).isNotNull();

        Optional<Parent> found = parentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Mary");
    }

    @Test
    @DisplayName("Find all parents")
    void testFindAll() {
        Parent p1 = Parent.builder().firstName("Jane").lastName("Doe").build();
        Parent p2 = Parent.builder().firstName("Ann").lastName("Smith").build();

        parentRepository.saveAll(List.of(p1, p2));

        var allParents = parentRepository.findAll();
        assertThat(allParents).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete parent")
    void testDelete() {
        Parent parent = Parent.builder().firstName("Delete").lastName("Me").build();
        parent = parentRepository.save(parent);
        Long id = parent.getId();

        parentRepository.delete(parent);
        Optional<Parent> deleted = parentRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}