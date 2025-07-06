package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeacherModelTest {

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    void testBuilderAndGetters() {
        Teacher teacher = Teacher.builder()
                .id(40L)
                .user(user)
                .build();

        assertThat(teacher.getId()).isEqualTo(40L);
        assertThat(teacher.getUser().getFirstName()).isEqualTo("John");
        assertThat(teacher.getUser().getLastName()).isEqualTo("Smith");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Teacher t1 = new Teacher();
        t1.setId(40L);
        t1.getUser().setFirstName("Mary");
        t1.getUser().setLastName("Jones");

        Teacher t2 = new Teacher();
        t2.setId(40L);
        t2.getUser().setFirstName("Mary");
        t2.getUser().setLastName("Jones");

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());

        t2.setId(41L);
        assertThat(t1).isNotEqualTo(t2);
    }

    @Test
    void testToStringNotNull() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.getUser().setFirstName("Sample");

        assertThat(teacher.toString()).isNotNull();
    }
}