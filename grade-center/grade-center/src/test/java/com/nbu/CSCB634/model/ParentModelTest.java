package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParentModelTest {

    private User user = User.builder()
                            .id(1L)
                            .username("johndoe")
                            .password("password")
                            .build();

    @Test
    void testBuilderAndGetters() {
        Parent parent = Parent.builder()
                .id(10L)
                .user(user)
                .build();

        assertThat(parent.getId()).isEqualTo(10L);
        assertThat(parent.getUser().getFirstName()).isEqualTo("Mary");
        assertThat(parent.getUser().getLastName()).isEqualTo("Poppins");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        Parent p1 = new Parent();
        p1.setId(10L);
        p1.getUser().setFirstName("Tom");
        p1.getUser().setLastName("Jones");

        Parent p2 = new Parent();
        p2.setId(10L);
        p2.getUser().setFirstName("Tom");
        p2.getUser().setLastName("Jones");

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());

        p2.setId(11L);
        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void testToStringNotNull() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.getUser().setFirstName("Test");

        assertThat(parent.toString()).isNotNull();
    }
}