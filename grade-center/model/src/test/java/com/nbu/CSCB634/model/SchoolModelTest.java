package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SchoolModelTest {

    @Test
    void testBuilderAndGetters() {
        School school = School.builder()
                .id(20L)
                .name("ABC Elementary")
                .address("123 Main Street")
                .build();

        assertThat(school.getId()).isEqualTo(20L);
        assertThat(school.getName()).isEqualTo("ABC Elementary");
        assertThat(school.getAddress()).isEqualTo("123 Main Street");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        School s1 = new School();
        s1.setId(20L);
        s1.setName("Test Name");
        s1.setAddress("Some Address");

        School s2 = new School();
        s2.setId(20L);
        s2.setName("Test Name");
        s2.setAddress("Some Address");

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());

        s2.setId(21L);
        assertThat(s1).isNotEqualTo(s2);
    }

    @Test
    void testToStringNotNull() {
        School school = new School();
        school.setId(1L);
        school.setName("Sample School");

        assertThat(school.toString()).isNotNull();
    }
}