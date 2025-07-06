package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ParentModelTest {

    @Test
    void testBuilderAndGetters() {
        User u = User.builder()
                .firstName("Mary")
                .lastName("Poppins")
                .build();
        Parent parent = Parent.builder()
                .id(10L).user(u)
                .build();

        assertThat(parent.getId()).isEqualTo(10L);
        assertThat(parent.getUser().getFirstName()).isEqualTo("Mary");
        assertThat(parent.getUser().getLastName()).isEqualTo("Poppins");
    }

    @Test
    void testSetterAndEqualsHashCode() {
        User u = User.builder()
                .firstName("Tom")
                .lastName("Jones")
                .build();
        Parent p1 = new Parent();
        p1.setId(10L);
        p1.setUser(u);

        Parent p2 = new Parent();
        p2.setId(10L);
        p1.setUser(u);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());

        p2.setId(11L);
        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void testToStringNotNull() {
        User u = User.builder()
                .firstName("Test")
                .lastName("Test")
                .build();
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setUser(u);

        assertThat(parent.toString()).isNotNull();
    }
}