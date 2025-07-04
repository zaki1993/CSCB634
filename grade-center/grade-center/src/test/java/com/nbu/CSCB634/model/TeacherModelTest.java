package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeacherModelTest {

    @Test
    void testBuilderAndGetters() {
        Teacher teacher = Teacher.builder()
                .id(40L)
                .firstName("John")
                .lastName("Smith")
                .build();

        assertThat(teacher.getId()).isEqualTo(40L);
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getLastName()).isEqualTo("Smith");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Teacher t1 = new Teacher();
        t1.setId(40L);
        t1.setFirstName("Mary");
        t1.setLastName("Jones");

        Teacher t2 = new Teacher();
        t2.setId(40L);
        t2.setFirstName("Mary");
        t2.setLastName("Jones");

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        t2.setId(41L);
        assertThat(t1).isNotEqualTo(t2);
    }

    @Test
    void testToStringNotNull() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Sample");

        assertThat(teacher.toString()).isNotNull();
    }
}