package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Absence;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.service.AbsenceService;
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
@RequestMapping("/parent/absences")
@RequiredArgsConstructor
public class ParentAbsenceController {

    private final AbsenceService absenceService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('PARENT')")
    public String listAbsencesForParent(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all absences for all children of this parent
        List<Absence> absences = absenceService.getAbsencesByParentId(currentUser.getId());
        
        // Get parent's children
        List<Student> children = absenceService.getStudentsForParent(currentUser.getId());
        
        // Group absences by student for better display
        Map<Student, List<Absence>> absencesByStudent = absences.stream()
                .collect(Collectors.groupingBy(Absence::getStudent));
        
        // Calculate absence statistics
        Map<Student, Long> justifiedCount = children.stream()
                .collect(Collectors.toMap(
                    child -> child,
                    child -> absences.stream()
                        .filter(abs -> abs.getStudent().equals(child) && Boolean.TRUE.equals(abs.getJustified()))
                        .count()
                ));
        
        Map<Student, Long> unjustifiedCount = children.stream()
                .collect(Collectors.toMap(
                    child -> child,
                    child -> absences.stream()
                        .filter(abs -> abs.getStudent().equals(child) && Boolean.FALSE.equals(abs.getJustified()))
                        .count()
                ));
        
        model.addAttribute("absences", absences);
        model.addAttribute("children", children);
        model.addAttribute("absencesByStudent", absencesByStudent);
        model.addAttribute("justifiedCount", justifiedCount);
        model.addAttribute("unjustifiedCount", unjustifiedCount);
        
        return "parent/absences/list";
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PARENT')")
    public String listAbsencesForChild(@PathVariable Long studentId, 
                                     Model model, 
                                     Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if parent can view this student
        if (!absenceService.canParentViewStudent(currentUser.getId(), studentId)) {
            throw new RuntimeException("Access denied - this is not your child");
        }
        
        // Get absences for specific child
        List<Absence> absences = absenceService.getAbsencesByParentAndStudent(currentUser.getId(), studentId);
        
        // Get parent's children for navigation
        List<Student> children = absenceService.getStudentsForParent(currentUser.getId());
        
        // Find current student
        Student currentStudent = children.stream()
                .filter(child -> child.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Calculate statistics for current student
        long justifiedCount = absences.stream()
                .filter(abs -> Boolean.TRUE.equals(abs.getJustified()))
                .count();
        long unjustifiedCount = absences.stream()
                .filter(abs -> Boolean.FALSE.equals(abs.getJustified()))
                .count();
        long pendingCount = absences.stream()
                .filter(abs -> abs.getJustified() == null)
                .count();
        
        model.addAttribute("absences", absences);
        model.addAttribute("children", children);
        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("justifiedCount", justifiedCount);
        model.addAttribute("unjustifiedCount", unjustifiedCount);
        model.addAttribute("pendingCount", pendingCount);
        
        return "parent/absences/by-student";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasRole('PARENT')")
    public String viewAbsence(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Absence absence = absenceService.getAbsenceById(id)
                .orElseThrow(() -> new RuntimeException("Absence not found"));
        
        // Check if parent can view this absence (belongs to their child)
        if (!absenceService.canParentViewStudent(currentUser.getId(), absence.getStudent().getId())) {
            throw new RuntimeException("Access denied - this absence does not belong to your child");
        }
        
        model.addAttribute("absence", absence);
        return "parent/absences/view";
    }
} 