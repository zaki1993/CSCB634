package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SubjectModelTest {

    @Test
    void testBuilderAndGetters() {
        Subject subject = Subject.builder()
                .id(50L)
                .name("Mathematics")
                .build();

        assertThat(subject.getId()).isEqualTo(50L);
        assertThat(subject.getName()).isEqualTo("Mathematics");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Subject s1 = new Subject();
        s1.setId(50L);
        s1.setName("Physics");

        Subject s2 = new Subject();
        s2.setId(50L);
        s2.setName("Physics");

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());

        s2.setId(51L);
        assertThat(s1).isNotEqualTo(s2);
    }

    @Test
    void testToStringNotNull() {
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Sample");

        assertThat(subject.toString()).isNotNull();
    }
}