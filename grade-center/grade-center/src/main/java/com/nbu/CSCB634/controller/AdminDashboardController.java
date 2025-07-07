package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.*;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final SchoolService schoolService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ParentService parentService;
    private final DirectorService directorService;
    private final SubjectService subjectService;
    private final GradeService gradeService;
    private final AbsenceService absenceService;
    private final AcademicTermService academicTermService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String dashboard(Model model) {
        try {
            // Get all data for global statistics
            List<User> allUsersRaw = userService.findAllUsers();
            List<School> allSchoolsRaw = schoolService.getAllSchools();
            List<Student> allStudentsRaw = studentService.getAllStudents();
            List<Teacher> allTeachersRaw = teacherService.getAllTeachers();
            List<Parent> allParentsRaw = parentService.getAllParents();
            List<Director> allDirectorsRaw = directorService.getAllDirectors();
            List<Subject> allSubjectsRaw = subjectService.getAllSubjects();
            List<AcademicTerm> allAcademicTermsRaw = academicTermService.getAllAcademicTerms();
            
            // Get all grades and absences across all schools
            List<Grade> allGradesRaw = gradeService.getAll();
            List<Absence> allAbsencesRaw = absenceService.getAll();
            
            // Create final variables with null checks
            final List<User> allUsers = allUsersRaw != null ? allUsersRaw : new java.util.ArrayList<>();
            final List<School> allSchools = allSchoolsRaw != null ? allSchoolsRaw : new java.util.ArrayList<>();
            final List<Student> allStudents = allStudentsRaw != null ? allStudentsRaw : new java.util.ArrayList<>();
            final List<Teacher> allTeachers = allTeachersRaw != null ? allTeachersRaw : new java.util.ArrayList<>();
            final List<Parent> allParents = allParentsRaw != null ? allParentsRaw : new java.util.ArrayList<>();
            final List<Director> allDirectors = allDirectorsRaw != null ? allDirectorsRaw : new java.util.ArrayList<>();
            final List<Subject> allSubjects = allSubjectsRaw != null ? allSubjectsRaw : new java.util.ArrayList<>();
            final List<AcademicTerm> allAcademicTerms = allAcademicTermsRaw != null ? allAcademicTermsRaw : new java.util.ArrayList<>();
            final List<Grade> allGrades = allGradesRaw != null ? allGradesRaw : new java.util.ArrayList<>();
            final List<Absence> allAbsences = allAbsencesRaw != null ? allAbsencesRaw : new java.util.ArrayList<>();
            
            // Basic counts
            model.addAttribute("totalUsers", allUsers.size());
            model.addAttribute("totalSchools", allSchools.size());
            model.addAttribute("totalStudents", allStudents.size());
            model.addAttribute("totalTeachers", allTeachers.size());
            model.addAttribute("totalParents", allParents.size());
            model.addAttribute("totalDirectors", allDirectors.size());
            model.addAttribute("totalSubjects", allSubjects.size());
            model.addAttribute("totalAcademicTerms", allAcademicTerms.size());
            model.addAttribute("totalGrades", allGrades.size());
            model.addAttribute("totalAbsences", allAbsences.size());
            
            // User role distribution
            Map<String, Long> roleDistribution = allUsers.stream()
                    .collect(Collectors.groupingBy(user -> user.getRole().name(), Collectors.counting()));
            model.addAttribute("roleDistribution", roleDistribution);
            
            // Grade statistics
            model.addAttribute("averageGrade", calculateAverageGrade(allGrades));
            model.addAttribute("gradeDistribution", calculateGradeDistribution(allGrades));
            
            // Absence statistics
            model.addAttribute("justifiedAbsences", allAbsences.stream()
                    .filter(abs -> Boolean.TRUE.equals(abs.getJustified()))
                    .count());
            model.addAttribute("unjustifiedAbsences", allAbsences.stream()
                    .filter(abs -> Boolean.FALSE.equals(abs.getJustified()))
                    .count());
            model.addAttribute("pendingAbsences", allAbsences.stream()
                    .filter(abs -> abs.getJustified() == null)
                    .count());
            
            // Statistics by school
            Map<String, Map<String, Object>> schoolStats = allSchools.stream()
                    .collect(Collectors.toMap(
                        School::getName,
                        school -> calculateSchoolStatistics(school, allStudents, allGrades, allAbsences)
                    ));
            model.addAttribute("schoolStats", schoolStats);
            
            // Statistics by subject
            Map<String, Map<String, Object>> subjectStats = allGrades.stream()
                    .filter(grade -> grade.getSubject() != null && grade.getSubject().getName() != null)
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
            
            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String viewUsers(Model model) {
        try {
            List<User> allUsersRaw = userService.findAllUsers();
            
            // Create final variable with null check
            final List<User> allUsers = allUsersRaw != null ? allUsersRaw : new java.util.ArrayList<>();
            
            // Group users by role for better display
            Map<String, List<User>> usersByRole = allUsers.stream()
                    .filter(user -> user.getRole() != null)
                    .collect(Collectors.groupingBy(user -> user.getRole().name()));
            
            model.addAttribute("usersByRole", usersByRole);
            model.addAttribute("totalUsers", allUsers.size());
            
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/schools")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String viewSchools(Model model) {
        try {
            List<School> allSchoolsRaw = schoolService.getAllSchools();
            List<Student> allStudentsRaw = studentService.getAllStudents();
            List<Teacher> allTeachersRaw = teacherService.getAllTeachers();
            List<Director> allDirectorsRaw = directorService.getAllDirectors();
            List<Grade> allGradesRaw = gradeService.getAll();
            List<Absence> allAbsencesRaw = absenceService.getAll();
            
            // Create final variables with null checks
            final List<School> allSchools = allSchoolsRaw != null ? allSchoolsRaw : new java.util.ArrayList<>();
            final List<Student> allStudents = allStudentsRaw != null ? allStudentsRaw : new java.util.ArrayList<>();
            final List<Teacher> allTeachers = allTeachersRaw != null ? allTeachersRaw : new java.util.ArrayList<>();
            final List<Director> allDirectors = allDirectorsRaw != null ? allDirectorsRaw : new java.util.ArrayList<>();
            final List<Grade> allGrades = allGradesRaw != null ? allGradesRaw : new java.util.ArrayList<>();
            final List<Absence> allAbsences = allAbsencesRaw != null ? allAbsencesRaw : new java.util.ArrayList<>();
            
            // Calculate detailed statistics for each school
            Map<School, Map<String, Object>> schoolDetailedStats = allSchools.stream()
                    .collect(Collectors.toMap(
                        school -> school,
                        school -> calculateSchoolStatistics(school, allStudents, allGrades, allAbsences)
                    ));
            
            model.addAttribute("schoolDetailedStats", schoolDetailedStats);
            model.addAttribute("totalSchools", allSchools.size());
            
            return "admin/schools";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading schools: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String viewStatistics(Model model) {
        try {
            List<Grade> allGradesRaw = gradeService.getAll();
            List<Absence> allAbsencesRaw = absenceService.getAll();
            List<School> allSchoolsRaw = schoolService.getAllSchools();
            List<Subject> allSubjectsRaw = subjectService.getAllSubjects();
            
            // Create final variables with null checks
            final List<Grade> allGrades = allGradesRaw != null ? allGradesRaw : new java.util.ArrayList<>();
            final List<Absence> allAbsences = allAbsencesRaw != null ? allAbsencesRaw : new java.util.ArrayList<>();
            final List<School> allSchools = allSchoolsRaw != null ? allSchoolsRaw : new java.util.ArrayList<>();
            final List<Subject> allSubjects = allSubjectsRaw != null ? allSubjectsRaw : new java.util.ArrayList<>();
            
            // Global statistics
            model.addAttribute("totalGrades", allGrades.size());
            model.addAttribute("totalAbsences", allAbsences.size());
            model.addAttribute("averageGrade", calculateAverageGrade(allGrades));
            model.addAttribute("gradeDistribution", calculateGradeDistribution(allGrades));
            
            // Statistics by school with detailed breakdown
            Map<String, Map<String, Object>> schoolStats = allSchools.stream()
                    .collect(Collectors.toMap(
                        School::getName,
                        school -> {
                            List<Student> schoolStudents = studentService.getStudentsBySchoolId(school.getId());
                            List<Grade> schoolGrades = schoolStudents.stream()
                                    .flatMap(student -> gradeService.getGradesByStudentId(student.getId()).stream())
                                    .collect(Collectors.toList());
                            List<Absence> schoolAbsences = schoolStudents.stream()
                                    .flatMap(student -> absenceService.getAbsencesByStudentId(student.getId()).stream())
                                    .collect(Collectors.toList());
                            
                            Map<String, Object> stats = new java.util.HashMap<>();
                            stats.put("studentCount", schoolStudents.size());
                            stats.put("gradeCount", schoolGrades.size());
                            stats.put("absenceCount", schoolAbsences.size());
                            stats.put("averageGrade", calculateAverageGrade(schoolGrades));
                            stats.put("gradeDistribution", calculateGradeDistribution(schoolGrades));
                            stats.put("schoolId", school.getId());  // Add school ID for links
                            
                            // Subject statistics within this school - add null checks
                            Map<String, Map<String, Object>> schoolSubjectStats = schoolGrades.stream()
                                    .filter(grade -> grade.getSubject() != null && grade.getSubject().getName() != null)
                                    .collect(Collectors.groupingBy(
                                        grade -> grade.getSubject().getName(),
                                        Collectors.collectingAndThen(
                                            Collectors.toList(),
                                            gradeList -> {
                                                Map<String, Object> subjectStats = new java.util.HashMap<>();
                                                subjectStats.put("count", gradeList.size());
                                                subjectStats.put("average", calculateAverageGrade(gradeList));
                                                return subjectStats;
                                            }
                                        )
                                    ));
                            stats.put("subjectStats", schoolSubjectStats);
                            
                            return stats;
                        }
                    ));
            model.addAttribute("schoolStats", schoolStats);
            
            // Global statistics by subject - add null checks
            Map<String, Map<String, Object>> globalSubjectStats = allGrades.stream()
                    .filter(grade -> grade.getSubject() != null && grade.getSubject().getName() != null)
                    .collect(Collectors.groupingBy(
                        grade -> grade.getSubject().getName(),
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            gradeList -> {
                                Map<String, Object> stats = new java.util.HashMap<>();
                                stats.put("count", gradeList.size());
                                stats.put("average", calculateAverageGrade(gradeList));
                                stats.put("distribution", calculateGradeDistribution(gradeList));
                                
                                // Statistics by school for this subject - add null checks
                                Map<String, Long> schoolDistribution = gradeList.stream()
                                        .filter(grade -> grade.getStudent() != null && 
                                                 grade.getStudent().getSchool() != null && 
                                                 grade.getStudent().getSchool().getName() != null)
                                        .collect(Collectors.groupingBy(
                                            grade -> grade.getStudent().getSchool().getName(),
                                            Collectors.counting()
                                        ));
                                stats.put("schoolDistribution", schoolDistribution);
                                
                                return stats;
                            }
                        )
                    ));
            model.addAttribute("globalSubjectStats", globalSubjectStats);
            
            return "admin/statistics";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading statistics: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/school/{schoolId}/statistics")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String viewSchoolStatistics(@PathVariable Long schoolId, Model model) {
        try {
            School school = schoolService.getSchoolById(schoolId)
                    .orElseThrow(() -> new RuntimeException("School not found"));
            
            List<Student> schoolStudentsRaw = studentService.getStudentsBySchoolId(schoolId);
            List<Grade> schoolGradesRaw = schoolStudentsRaw.stream()
                    .flatMap(student -> gradeService.getGradesByStudentId(student.getId()).stream())
                    .collect(Collectors.toList());
            List<Absence> schoolAbsencesRaw = schoolStudentsRaw.stream()
                    .flatMap(student -> absenceService.getAbsencesByStudentId(student.getId()).stream())
                    .collect(Collectors.toList());
            
            // Create final variables with null checks
            final List<Student> schoolStudents = schoolStudentsRaw != null ? schoolStudentsRaw : new java.util.ArrayList<>();
            final List<Grade> schoolGrades = schoolGradesRaw != null ? schoolGradesRaw : new java.util.ArrayList<>();
            final List<Absence> schoolAbsences = schoolAbsencesRaw != null ? schoolAbsencesRaw : new java.util.ArrayList<>();
            
            model.addAttribute("school", school);
            model.addAttribute("studentCount", schoolStudents.size());
            model.addAttribute("gradeCount", schoolGrades.size());
            model.addAttribute("absenceCount", schoolAbsences.size());
            model.addAttribute("averageGrade", calculateAverageGrade(schoolGrades));
            model.addAttribute("gradeDistribution", calculateGradeDistribution(schoolGrades));
            
            // Subject statistics for this school - add null checks
            Map<String, Map<String, Object>> subjectStats = schoolGrades.stream()
                    .filter(grade -> grade.getSubject() != null && grade.getSubject().getName() != null)
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
            
            return "admin/school-statistics";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading school statistics: " + e.getMessage());
            return "error";
        }
    }

    // Helper methods
    private Map<String, Object> calculateSchoolStatistics(School school, List<Student> allStudents, 
                                                         List<Grade> allGrades, List<Absence> allAbsences) {
        try {
            // Add null checks for all collections and input parameters
            if (school == null) {
                return new java.util.HashMap<>();
            }
            
            // Create final variables with null checks for lambda safety
            final List<Student> finalAllStudents = allStudents != null ? allStudents : new java.util.ArrayList<>();
            final List<Grade> finalAllGrades = allGrades != null ? allGrades : new java.util.ArrayList<>();
            final List<Absence> finalAllAbsences = allAbsences != null ? allAbsences : new java.util.ArrayList<>();
            
            List<Student> schoolStudents = finalAllStudents.stream()
                    .filter(student -> student != null && student.getSchool() != null && 
                             student.getSchool().getId() != null && 
                             student.getSchool().getId().equals(school.getId()))
                    .collect(Collectors.toList());
            
            List<Grade> schoolGrades = finalAllGrades.stream()
                    .filter(grade -> grade != null && grade.getStudent() != null && 
                             grade.getStudent().getSchool() != null && 
                             grade.getStudent().getSchool().getId() != null &&
                             grade.getStudent().getSchool().getId().equals(school.getId()))
                    .collect(Collectors.toList());
            
            List<Absence> schoolAbsences = finalAllAbsences.stream()
                    .filter(absence -> absence != null && absence.getStudent() != null && 
                             absence.getStudent().getSchool() != null && 
                             absence.getStudent().getSchool().getId() != null &&
                             absence.getStudent().getSchool().getId().equals(school.getId()))
                    .collect(Collectors.toList());
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("studentCount", schoolStudents.size());
            stats.put("gradeCount", schoolGrades.size());
            stats.put("absenceCount", schoolAbsences.size());
            stats.put("averageGrade", calculateAverageGrade(schoolGrades));
            stats.put("gradeDistribution", calculateGradeDistribution(schoolGrades));
            stats.put("schoolId", school.getId());  // Add school ID for links
            
            return stats;
        } catch (Exception e) {
            // Return empty stats in case of error
            Map<String, Object> emptyStats = new java.util.HashMap<>();
            emptyStats.put("studentCount", 0);
            emptyStats.put("gradeCount", 0);
            emptyStats.put("absenceCount", 0);
            emptyStats.put("averageGrade", 0.0);
            emptyStats.put("gradeDistribution", new java.util.HashMap<>());
            emptyStats.put("schoolId", school != null ? school.getId() : 0);
            return emptyStats;
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