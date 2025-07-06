package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DirectorModelTest {

    @Test
    void testBuilderAndGetters() {
        User u = User.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        Director director = Director.builder()
                .id(1L)
                .user(u)
                .build();

        assertThat(director.getId()).isEqualTo(1L);
        assertThat(director.getUser().getFirstName()).isEqualTo("John");
        assertThat(director.getUser().getLastName()).isEqualTo("Doe");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        User u = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();
        Director dir1 = new Director();
        dir1.setId(1L);
        dir1.setUser(u);

        Director dir2 = new Director();
        dir2.setId(1L);
        dir2.setUser(u);

        assertThat(dir1).isEqualTo(dir2);
        assertThat(dir1.hashCode()).isEqualTo(dir2.hashCode());

        // Change id to check inequality
        dir2.setId(2L);
        assertThat(dir1).isNotEqualTo(dir2);
    }

    @Test
    void testToStringNotNull() {
        User u = User.builder()
                .firstName("Test")
                .lastName("User")
                .build();
        Director director = new Director();
        director.setId(1L);
        director.setUser(u);

        assertThat(director.toString()).isNotNull();
    }
}