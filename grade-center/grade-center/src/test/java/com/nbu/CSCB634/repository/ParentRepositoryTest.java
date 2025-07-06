package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    @DisplayName("Save parent and find by ID")
    void testSaveAndFindById() {
        Parent parent = Parent.builder()
                .user(user)
                .build();

        Parent saved = parentRepository.save(parent);
        assertThat(saved.getId()).isNotNull();

        Optional<Parent> found = parentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getFirstName()).isEqualTo("Mary");
    }

    @Test
    @DisplayName("Find all parents")
    void testFindAll() {
        Parent p1 = Parent.builder().user(user).build();
        Parent p2 = Parent.builder().user(user).build();

        parentRepository.saveAll(List.of(p1, p2));

        var allParents = parentRepository.findAll();
        assertThat(allParents).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete parent")
    void testDelete() {
        Parent parent = Parent.builder().user(user).build();
        parent = parentRepository.save(parent);
        Long id = parent.getId();

        parentRepository.delete(parent);
        Optional<Parent> deleted = parentRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}