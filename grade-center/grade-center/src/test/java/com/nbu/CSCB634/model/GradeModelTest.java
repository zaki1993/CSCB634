package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GradeModelTest {

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    void testBuilderAndGetters() {
        Student student = Student.builder().id(100L).user(user).build();
        Subject subject = Subject.builder().id(10L).name("Math").build();

        Grade grade = Grade.builder()
                .id(1L)
                .student(student)
                .subject(subject)
                .value(5)
                .dateAwarded(LocalDate.of(2024, 1, 1))
                .build();

        assertThat(grade.getId()).isEqualTo(1L);
        assertThat(grade.getStudent()).isEqualTo(student);
        assertThat(grade.getSubject()).isEqualTo(subject);
        assertThat(grade.getValue()).isEqualTo(5);
        assertThat(grade.getDateAwarded()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Grade g1 = new Grade();
        g1.setId(2L);
        g1.setValue(4);

        Grade g2 = new Grade();
        g2.setId(2L);
        g2.setValue(4);

        assertThat(g1).isEqualTo(g2);
        assertThat(g1.hashCode()).isEqualTo(g2.hashCode());

        g2.setId(3L);
        assertThat(g1).isNotEqualTo(g2);
    }

    @Test
    void testToStringNotNull() {
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setValue(3);

        assertThat(grade.toString()).isNotNull();
    }
}