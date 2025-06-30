package com.nbu.CSCB869.model;

import com.nbu.CSCB869.model.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class StudentTest {

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("John Doe", "123456");
    }

    @Test
    void testConstructor() {
        assertNotNull(student);
        assertEquals("John Doe", student.getName());
        assertEquals("123456", student.getFn());
    }

    @Test
    void testSetName() {
        student.setName("Jane Doe");
        assertEquals("Jane Doe", student.getName());
    }

    @Test
    void testSetFn() {
        student.setFn("654321");
        assertEquals("654321", student.getFn());
    }

    @Test
    void testUsernameField() {
        student.setUsername("johndoe");
        assertEquals("johndoe", student.getUsername());
    }

    @Test
    void testUserRelationship() {
        User user = new User();
        user.setUserName("johndoe");
        student.setUser(user);

        assertNotNull(student.getUser());
        assertEquals("johndoe", student.getUser().getUsername());
    }
}
