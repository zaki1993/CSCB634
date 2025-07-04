package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentModelTest {

    @Test
    void testBuilderAndGetters() {
        Student student = Student.builder()
                .id(30L)
                .firstName("Alice")
                .lastName("Wonderland")
                .build();

        assertThat(student.getId()).isEqualTo(30L);
        assertThat(student.getFirstName()).isEqualTo("Alice");
        assertThat(student.getLastName()).isEqualTo("Wonderland");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Student s1 = new Student();
        s1.setId(30L);
        s1.setFirstName("Bob");
        s1.setLastName("Builder");

        Student s2 = new Student();
        s2.setId(30L);
        s2.setFirstName("Bob");
        s2.setLastName("Builder");

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());

        s2.setId(31L);
        assertThat(s1).isNotEqualTo(s2);
    }

    @Test
    void testToStringNotNull() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Sample");

        assertThat(student.toString()).isNotNull();
    }
}