package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
    void testToStringNotNull() {
        School school = new School();
        school.setId(1L);
        school.setName("Sample School");

        assertThat(school.toString()).isNotNull();
    }
}