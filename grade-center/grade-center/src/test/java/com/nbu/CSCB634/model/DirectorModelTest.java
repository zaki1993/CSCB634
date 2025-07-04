package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectorModelTest {

    @Test
    void testBuilderAndGetters() {
        Director director = Director.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        assertThat(director.getId()).isEqualTo(1L);
        assertThat(director.getFirstName()).isEqualTo("John");
        assertThat(director.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Director dir1 = new Director();
        dir1.setId(1L);
        dir1.setFirstName("Jane");
        dir1.setLastName("Smith");

        Director dir2 = new Director();
        dir2.setId(1L);
        dir2.setFirstName("Jane");
        dir2.setLastName("Smith");

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
        director.setFirstName("Test");
        director.setLastName("User");

        assertThat(director.toString()).isNotNull();
    }
}