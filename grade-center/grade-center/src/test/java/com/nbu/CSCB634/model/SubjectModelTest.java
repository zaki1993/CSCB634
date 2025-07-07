package com.nbu.CSCB634.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
    void testToStringNotNull() {
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Sample");

        assertThat(subject.toString()).isNotNull();
    }
}