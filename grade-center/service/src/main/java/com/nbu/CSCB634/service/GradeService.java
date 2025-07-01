package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.repository.GradeRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;

    @PreAuthorize("hasRole('TEACHER')")
    public Grade createGrade(@Valid Grade grade) {
        return gradeRepository.save(grade);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT')")
    public List<Grade> getGradesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return gradeRepository.findByStudent(student);
    }

    @PreAuthorize("hasRole('TEACHER')")
    public Grade updateGrade(Long id, @Valid Grade updatedGrade) {
        return gradeRepository.findById(id)
                .map(grade -> {
                    grade.setValue(updatedGrade.getValue());
                    grade.setDateAwarded(updatedGrade.getDateAwarded());
                    grade.setSubject(updatedGrade.getSubject());
                    // teacher/student usually not updated here
                    return gradeRepository.save(grade);
                })
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));
    }

    @PreAuthorize("hasRole('TEACHER')")
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));
        gradeRepository.delete(grade);
    }
}