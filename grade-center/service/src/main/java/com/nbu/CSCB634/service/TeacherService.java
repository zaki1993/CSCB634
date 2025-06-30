package com.nbu.CSCB634.service;

import com.nbu.CSCB634.repository.TeacherRepository;
import com.nbu.CSCB634.repository.diploma.defense.DiplomaDefenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing Teacher entities.
 * Provides methods to retrieve and manage teacher data.
 */
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DiplomaDefenseRepository diplomaDefenseRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, DiplomaDefenseRepository diplomaDefenseRepository) {
        this.teacherRepository = teacherRepository;
        this.diplomaDefenseRepository = diplomaDefenseRepository;
    }

    /**
     * Retrieves all teachers in the system.
     *
     * @return List of all teachers
     */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * Finds a teacher by their ID.
     *
     * @param id ID of the teacher
     * @return The teacher, if found
     */
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

    /**
     * Saves or updates a teacher record.
     *
     * @param teacher Teacher entity to save
     * @return Saved teacher entity
     */
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    /**
     * Deletes a teacher by ID.
     *
     * @param id ID of the teacher to delete
     */
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }

    public long getSuccessfulDefensesByTeacher(Long teacherId) {
        return diplomaDefenseRepository.countSuccessfulDefensesByTeacher(teacherId);
    }
}
