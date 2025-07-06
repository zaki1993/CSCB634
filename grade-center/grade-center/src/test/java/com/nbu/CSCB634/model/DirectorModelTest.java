package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DirectorModelTest {

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    void testBuilderAndGetters() {
        Director director = Director.builder()
                .id(1L)
                .user(user)
                .build();

        assertThat(director.getId()).isEqualTo(1L);
        assertThat(director.getUser().getFirstName()).isEqualTo("John");
        assertThat(director.getUser().getLastName()).isEqualTo("Doe");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Director dir1 = new Director();
        dir1.setId(1L);
        dir1.getUser().setFirstName("Jane");
        dir1.getUser().setLastName("Smith");

        Director dir2 = new Director();
        dir2.setId(1L);
        dir2.getUser().setFirstName("Jane");
        dir2.getUser().setLastName("Smith");

        assertThat(dir1).isEqualTo(dir2);
        assertThat(dir1.hashCode()).isEqualTo(dir2.hashCode());

        // Change id to check inequality
        dir2.setId(2L);
        assertThat(dir1).isNotEqualTo(dir2);
    }

    @Test
    void testToStringNotNull() {
        Director director = new Director();
        director.setId(1L);
        director.getUser().setFirstName("Test");
        director.getUser().setLastName("User");

        assertThat(director.toString()).isNotNull();
    }
}