package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TeacherTest {

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("Dr. Smith", RoleType.ASSISTANT);
    }

    @Test
    void testConstructor() {
        assertNotNull(teacher);
        assertEquals("Dr. Smith", teacher.getName());
        assertEquals(RoleType.ASSISTANT, teacher.getType());
    }

    @Test
    void testSetName() {
        teacher.setName("Dr. Brown");
        assertEquals("Dr. Brown", teacher.getName());
    }

    @Test
    void testSetType() {
        teacher.setType(RoleType.PROFESSOR);
        assertEquals(RoleType.PROFESSOR, teacher.getType());
    }

    @Test
    void testUsernameField() {
        teacher.setUsername("drsmith");
        assertEquals("drsmith", teacher.getUsername());
    }

    @Test
    void testUserRelationship() {
        User user = new User();
        user.setUserName("drsmith");
        teacher.setUser(user);

        assertNotNull(teacher.getUser());
        assertEquals("drsmith", teacher.getUser().getUsername());
    }
}
