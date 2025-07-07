package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.*;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/director/dashboard")
@RequiredArgsConstructor
public class DirectorDashboardController {

    private final DirectorService directorService;
    private final UserService userService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ParentService parentService;
    private final SubjectService subjectService;
    private final GradeService gradeService;
    private final AbsenceService absenceService;
    private final AcademicTermService academicTermService;

    @GetMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public String dashboard(Model model, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Director director = directorService.getDirectorByUserId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Director not found"));
            
            School school = director.getSchool();
            
            // Basic school information
            model.addAttribute("school", school);
            model.addAttribute("director", director);
            
            // Get all entities for this school
            List<Student> studentsRaw = studentService.getStudentsBySchoolId(school.getId());
            List<Teacher> teachersRaw = teacherService.getTeachersBySchoolId(school.getId());
            List<Parent> parentsRaw = getParentsBySchoolId(school.getId());
            List<Subject> subjectsRaw = subjectService.getAllSubjects();
            List<AcademicTerm> academicTermsRaw = academicTermService.getAcademicTermsBySchoolId(school.getId());
            
            // Create final variables with null checks
            final List<Student> students = studentsRaw != null ? studentsRaw : new java.util.ArrayList<>();
            final List<Teacher> teachers = teachersRaw != null ? teachersRaw : new java.util.ArrayList<>();
            final List<Parent> parents = parentsRaw != null ? parentsRaw : new java.util.ArrayList<>();
            final List<Subject> subjects = subjectsRaw != null ? subjectsRaw : new java.util.ArrayList<>();
            final List<AcademicTerm> academicTerms = academicTermsRaw != null ? academicTermsRaw : new java.util.ArrayList<>();
            
            // Basic counts
            model.addAttribute("studentsCount", students.size());
            model.addAttribute("teachersCount", teachers.size());
            model.addAttribute("parentsCount", parents.size());
            model.addAttribute("subjectsCount", subjects.size());
            model.addAttribute("academicTermsCount", academicTerms.size());
            
            // Get grades for statistics
            List<Grade> allGradesRaw = students.stream()
                    .flatMap(student -> gradeService.getGradesByStudentId(student.getId()).stream())
                    .collect(Collectors.toList());
            
            // Get absences for statistics
            List<Absence> allAbsencesRaw = students.stream()
                    .flatMap(student -> absenceService.getAbsencesByStudentId(student.getId()).stream())
                    .collect(Collectors.toList());
            
            // Create final variables for lambda safety
            final List<Grade> allGrades = allGradesRaw != null ? allGradesRaw : new java.util.ArrayList<>();
            final List<Absence> allAbsences = allAbsencesRaw != null ? allAbsencesRaw : new java.util.ArrayList<>();
            
            // Grade statistics - add null checks
            model.addAttribute("totalGrades", allGrades.size());
            model.addAttribute("averageGrade", calculateAverageGrade(allGrades));
            model.addAttribute("gradeDistribution", calculateGradeDistribution(allGrades));
            
            // Absence statistics
            model.addAttribute("totalAbsences", allAbsences.size());
            model.addAttribute("justifiedAbsences", allAbsences.stream()
                    .filter(abs -> Boolean.TRUE.equals(abs.getJustified()))
                    .count());
            model.addAttribute("unjustifiedAbsences", allAbsences.stream()
                    .filter(abs -> Boolean.FALSE.equals(abs.getJustified()))
                    .count());
            
            // Subject statistics
            Map<String, List<Grade>> gradesBySubject = allGrades.stream()
                    .filter(grade -> grade.getSubject() != null && grade.getSubject().getName() != null)
                    .collect(Collectors.groupingBy(grade -> grade.getSubject().getName()));
            model.addAttribute("gradesBySubject", gradesBySubject);
            
            // Teacher statistics
            Map<String, List<Grade>> gradesByTeacher = allGrades.stream()
                    .filter(grade -> grade.getTeacher() != null && grade.getTeacher().getUser() != null &&
                             grade.getTeacher().getUser().getFirstName() != null && 
                             grade.getTeacher().getUser().getLastName() != null)
                    .collect(Collectors.groupingBy(grade -> 
                        grade.getTeacher().getUser().getFirstName() + " " + grade.getTeacher().getUser().getLastName()));
            model.addAttribute("gradesByTeacher", gradesByTeacher);
            
            // Recent data
            model.addAttribute("recentGrades", allGrades.stream()
                    .filter(grade -> grade.getDateAwarded() != null)
                    .sorted((g1, g2) -> g2.getDateAwarded().compareTo(g1.getDateAwarded()))
                    .limit(10)
                    .collect(Collectors.toList()));
            
            model.addAttribute("recentAbsences", allAbsences.stream()
                    .filter(absence -> absence.getAbsenceDate() != null)
                    .sorted((a1, a2) -> a2.getAbsenceDate().compareTo(a1.getAbsenceDate()))
                    .limit(10)
                    .collect(Collectors.toList()));
            
            return "director/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String viewStudents(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Director director = directorService.getDirectorByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Director not found"));
        
        List<Student> students = studentService.getStudentsBySchoolId(director.getSchool().getId());
        
        model.addAttribute("students", students);
        model.addAttribute("school", director.getSchool());
        
        return "director/students";
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String viewTeachers(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Director director = directorService.getDirectorByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Director not found"));
        
        List<Teacher> teachers = teacherService.getTeachersBySchoolId(director.getSchool().getId());
        
        model.addAttribute("teachers", teachers);
        model.addAttribute("school", director.getSchool());
        
        return "director/teachers";
    }

    @GetMapping("/parents")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String viewParents(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Director director = directorService.getDirectorByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Director not found"));
        
        List<Parent> parents = getParentsBySchoolId(director.getSchool().getId());
        
        model.addAttribute("parents", parents);
        model.addAttribute("school", director.getSchool());
        
        return "director/parents";
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String viewStatistics(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Director director = directorService.getDirectorByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Director not found"));
        
        School school = director.getSchool();
        List<Student> students = studentService.getStudentsBySchoolId(school.getId());
        
        // Get all grades for detailed statistics
        List<Grade> allGradesRaw = students.stream()
                .flatMap(student -> gradeService.getGradesByStudentId(student.getId()).stream())
                .collect(Collectors.toList());
        
        // Get all absences for detailed statistics
        List<Absence> allAbsencesRaw = students.stream()
                .flatMap(student -> absenceService.getAbsencesByStudentId(student.getId()).stream())
                .collect(Collectors.toList());
        
        // Create final variables for lambda safety
        final List<Grade> allGrades = allGradesRaw != null ? allGradesRaw : new java.util.ArrayList<>();
        final List<Absence> allAbsences = allAbsencesRaw != null ? allAbsencesRaw : new java.util.ArrayList<>();
        
        // Detailed statistics
        model.addAttribute("school", school);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalGrades", allGrades.size());
        model.addAttribute("totalAbsences", allAbsences.size());
        model.addAttribute("averageGrade", calculateAverageGrade(allGrades));
        model.addAttribute("gradeDistribution", calculateGradeDistribution(allGrades));
        
        // Statistics by subject
        Map<String, Map<String, Object>> subjectStats = allGrades.stream()
                .collect(Collectors.groupingBy(
                    grade -> grade.getSubject().getName(),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        gradeList -> {
                            Map<String, Object> stats = new java.util.HashMap<>();
                            stats.put("count", gradeList.size());
                            stats.put("average", calculateAverageGrade(gradeList));
                            stats.put("distribution", calculateGradeDistribution(gradeList));
                            return stats;
                        }
                    )
                ));
        model.addAttribute("subjectStats", subjectStats);
        
        // Statistics by teacher
        Map<String, Map<String, Object>> teacherStats = allGrades.stream()
                .collect(Collectors.groupingBy(
                    grade -> grade.getTeacher().getUser().getFirstName() + " " + grade.getTeacher().getUser().getLastName(),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        gradeList -> {
                            Map<String, Object> stats = new java.util.HashMap<>();
                            stats.put("count", gradeList.size());
                            stats.put("average", calculateAverageGrade(gradeList));
                            stats.put("distribution", calculateGradeDistribution(gradeList));
                            return stats;
                        }
                    )
                ));
        model.addAttribute("teacherStats", teacherStats);
        
        return "director/statistics";
    }

    // Helper methods
    private List<Parent> getParentsBySchoolId(Long schoolId) {
        try {
            List<Student> studentsRaw = studentService.getStudentsBySchoolId(schoolId);
            List<Parent> allParentsRaw = parentService.getAllParents();
            
            // Create final variables for lambda safety
            final List<Student> students = studentsRaw != null ? studentsRaw : new java.util.ArrayList<>();
            final List<Parent> allParents = allParentsRaw != null ? allParentsRaw : new java.util.ArrayList<>();
            
            return allParents.stream()
                    .filter(parent -> parent.getStudents() != null && 
                        parent.getStudents().stream().anyMatch(student -> 
                            student.getSchool() != null && student.getSchool().getId().equals(schoolId)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    private double calculateAverageGrade(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;
        return grades.stream()
                .mapToDouble(Grade::getValue)
                .average()
                .orElse(0.0);
    }

    private Map<Integer, Long> calculateGradeDistribution(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return new java.util.HashMap<>();
        }
        return grades.stream()
                .collect(Collectors.groupingBy(
                    Grade::getValue,
                    Collectors.counting()
                ));
    }
} 