package com.nbu.CSCB634.model.diploma.defense;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DiplomaDefenseTest {

    private DiplomaDefense defense;

    @BeforeEach
    void setUp() {
        defense = new DiplomaDefense();
        defense.setId(1L);
        defense.setDefenseDate(LocalDateTime.of(2025, 1, 15, 10, 0));
        defense.setLocation("Room 101");
    }

    @Test
    void testIsReadOnly_NoGrades() {
        defense.setGradles(new HashMap<>()); // Empty grades map
        assertFalse(defense.isReadOnly(), "Defense should not be read-only if there are no grades.");
    }

    @Test
    void testIsReadOnly_WithGrades() {
        Map<Long, Double> grades = new HashMap<>();
        grades.put(101L, 5.0);
        defense.setGradles(grades);

        assertTrue(defense.isReadOnly(), "Defense should be read-only if there are grades.");
    }

    @Test
    void testAddThesis() {
        List<Long> theses = new ArrayList<>();
        theses.add(101L);
        theses.add(102L);
        defense.setTheses(theses);

        assertEquals(2, defense.getTheses().size(), "Theses list should contain two entries.");
        assertTrue(defense.getTheses().contains(101L), "Theses list should contain ID 101.");
        assertTrue(defense.getTheses().contains(102L), "Theses list should contain ID 102.");
    }

    @Test
    void testAddTeacher() {
        List<Long> teachers = new ArrayList<>();
        teachers.add(201L);
        teachers.add(202L);
        defense.setTeachers(teachers);

        assertEquals(2, defense.getTeachers().size(), "Teachers list should contain two entries.");
        assertTrue(defense.getTeachers().contains(201L), "Teachers list should contain ID 201.");
        assertTrue(defense.getTeachers().contains(202L), "Teachers list should contain ID 202.");
    }

    @Test
    void testAddStudent() {
        List<Long> students = new ArrayList<>();
        students.add(301L);
        students.add(302L);
        defense.setStudents(students);

        assertEquals(2, defense.getStudents().size(), "Students list should contain two entries.");
        assertTrue(defense.getStudents().contains(301L), "Students list should contain ID 301.");
        assertTrue(defense.getStudents().contains(302L), "Students list should contain ID 302.");
    }

    @Test
    void testAssignGrade() {
        Map<Long, Double> grades = new HashMap<>();
        grades.put(101L, 5.5);
        defense.setGradles(grades);

        assertEquals(1, defense.getGradles().size(), "Grades map should contain one entry.");
        assertEquals(5.5, defense.getGradles().get(101L), "Grade for thesis ID 101 should be 5.5.");
    }

    @Test
    void testOverrideGrade() {
        Map<Long, Double> grades = new HashMap<>();
        grades.put(101L, 5.5);
        defense.setGradles(grades);

        // Override the grade
        defense.getGradles().put(101L, 6.0);

        assertEquals(6.0, defense.getGradles().get(101L), "Grade for thesis ID 101 should be updated to 6.0.");
    }
}
