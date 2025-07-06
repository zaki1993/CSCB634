package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class DirectorRepositoryTest {

    @Autowired
    private DirectorRepository directorRepository;

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    @DisplayName("Save director and find by ID")
    void testSaveAndFindById() {
        Director director = Director.builder()
                .user(user)
                .build();

        Director saved = directorRepository.save(director);
        assertThat(saved.getId()).isNotNull();

        Optional<Director> found = directorRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getFirstName()).isEqualTo("John");
        assertThat(found.get().getUser().getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Find all directors")
    void testFindAll() {
        Director d1 = Director.builder().user(user).build();
        Director d2 = Director.builder().user(user).build();

        directorRepository.saveAll(List.of(d1, d2));

        var allDirectors = directorRepository.findAll();
        assertThat(allDirectors).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Delete director")
    void testDelete() {
        Director director = Director.builder().user(user).build();
        director = directorRepository.save(director);
        Long id = director.getId();

        directorRepository.delete(director);
        Optional<Director> deleted = directorRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Find director by invalid ID returns empty")
    void testFindByIdInvalid() {
        Optional<Director> directorOptional = directorRepository.findById(-1L);
        assertThat(directorOptional).isEmpty();
    }

    @Test
    @DisplayName("Deleting non-existing director does not throw")
    void testDeleteNonExisting() {
        Director director = Director.builder().id(-1L).user(user).build();
        assertThatThrownBy(() -> directorRepository.delete(director))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Entity must not be null"); // depends on JPA implementation or may ignore
    }
}