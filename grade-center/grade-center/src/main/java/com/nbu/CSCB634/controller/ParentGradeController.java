package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.service.GradeService;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parent/grades")
@RequiredArgsConstructor
public class ParentGradeController {

    private final GradeService gradeService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('PARENT')")
    public String listGradesForParent(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all grades for all children of this parent
        List<Grade> grades = gradeService.getGradesByParentId(currentUser.getId());
        
        // Get parent's children
        List<Student> children = gradeService.getStudentsForParent(currentUser.getId());
        
        // Group grades by student for better display
        Map<Student, List<Grade>> gradesByStudent = grades.stream()
                .collect(Collectors.groupingBy(Grade::getStudent));
        
        model.addAttribute("grades", grades);
        model.addAttribute("children", children);
        model.addAttribute("gradesByStudent", gradesByStudent);
        
        return "parent/grades/list";
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PARENT')")
    public String listGradesForChild(@PathVariable Long studentId, 
                                   Model model, 
                                   Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if parent can view this student
        if (!gradeService.canParentViewStudent(currentUser.getId(), studentId)) {
            throw new RuntimeException("Access denied - this is not your child");
        }
        
        // Get grades for specific child
        List<Grade> grades = gradeService.getGradesByParentAndStudent(currentUser.getId(), studentId);
        
        // Get parent's children for navigation
        List<Student> children = gradeService.getStudentsForParent(currentUser.getId());
        
        // Find current student
        Student currentStudent = children.stream()
                .filter(child -> child.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Group grades by subject for better display
        Map<String, List<Grade>> gradesBySubject = grades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getSubject().getName()));
        
        model.addAttribute("grades", grades);
        model.addAttribute("children", children);
        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("gradesBySubject", gradesBySubject);
        
        return "parent/grades/by-student";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasRole('PARENT')")
    public String viewGrade(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Grade grade = gradeService.getGradeById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        // Check if parent can view this grade (belongs to their child)
        if (!gradeService.canParentViewStudent(currentUser.getId(), grade.getStudent().getId())) {
            throw new RuntimeException("Access denied - this grade does not belong to your child");
        }
        
        model.addAttribute("grade", grade);
        return "parent/grades/view";
    }
} 