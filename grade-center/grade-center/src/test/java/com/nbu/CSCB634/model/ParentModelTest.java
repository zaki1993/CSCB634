package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ParentModelTest {

    @Test
    void testBuilderAndGetters() {
        Parent parent = Parent.builder()
                .id(10L)
                .firstName("Mary")
                .lastName("Poppins")
                .build();

        assertThat(parent.getId()).isEqualTo(10L);
        assertThat(parent.getFirstName()).isEqualTo("Mary");
        assertThat(parent.getLastName()).isEqualTo("Poppins");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Parent p1 = new Parent();
        p1.setId(10L);
        p1.setFirstName("Tom");
        p1.setLastName("Jones");

        Parent p2 = new Parent();
        p2.setId(10L);
        p2.setFirstName("Tom");
        p2.setLastName("Jones");

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());

        p2.setId(11L);
        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void testToStringNotNull() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFirstName("Test");

        assertThat(parent.toString()).isNotNull();
    }
}