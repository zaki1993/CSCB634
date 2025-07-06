package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public Teacher createTeacher(@Valid Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public Teacher updateTeacher(Long id, @Valid Teacher updatedTeacher) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacher.getUser().setFirstName(updatedTeacher.getUser().getFirstName());
                    teacher.getUser().setLastName(updatedTeacher.getUser().getLastName());
                    teacher.setQualifiedSubjects(updatedTeacher.getQualifiedSubjects());
                    teacher.setSchool(updatedTeacher.getSchool());
                    return teacherRepository.save(teacher);
                })
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        teacherRepository.delete(teacher);
    }
}