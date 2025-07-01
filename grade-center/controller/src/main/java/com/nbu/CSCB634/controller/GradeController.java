package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        Optional<Grade> gradeOpt = gradeService.getGradeById(id);
        return gradeOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT')")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId) {
        try {
            List<Grade> grades = gradeService.getGradesByStudentId(studentId);
            return ResponseEntity.ok(grades);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Grade> createGrade(@Valid @RequestBody Grade grade) {
        Grade createdGrade = gradeService.createGrade(grade);
        return ResponseEntity.ok(createdGrade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Grade> updateGrade(@PathVariable Long id,
                                             @Valid @RequestBody Grade grade) {
        try {
            Grade updatedGrade = gradeService.updateGrade(id, grade);
            return ResponseEntity.ok(updatedGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        try {
            gradeService.deleteGrade(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}