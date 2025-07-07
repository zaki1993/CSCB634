package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.GradeDto;
import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.*;
import com.nbu.CSCB634.global.AccessControlConfig;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeWebController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final AcademicTermService academicTermService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String listGrades(Model model, Authentication authentication) {
        List<Grade> grades;
        
        if (AccessControlConfig.isTeacher()) {
            // Teacher sees only their grades
            User currentUser = userService.findByUsername(authentication.getName())
                                          .orElseThrow(() -> new RuntimeException("User not found"));
            grades = gradeService.getGradesByTeacherId(currentUser.getId());
        } else {
            // Admin/Director sees all grades
            grades = gradeService.getAll();
        }
        
        model.addAttribute("grades", grades);
        return "grades/list";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String viewGrade(@PathVariable Long id, Model model, Authentication authentication) {
        Grade grade = gradeService.getGradeById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        // Check if teacher can view this grade
        if (AccessControlConfig.isTeacher()) {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!grade.getTeacher().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied");
            }
        }
        
        model.addAttribute("grade", grade);
        return "grades/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createGradeForm(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("gradeDto", new GradeDto());
        
        // Get students that this teacher can manage
        List<Student> students = gradeService.getStudentsForTeacher(currentUser.getId());
        model.addAttribute("students", students);
        
        // Get teacher's qualified subjects
        Teacher teacher = teacherService.getTeacherById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        model.addAttribute("subjects", teacher.getQualifiedSubjects());
        
        // Get academic terms for teacher's school
        List<AcademicTerm> academicTerms = academicTermService.getAllAcademicTerms()
                .stream()
                .filter(term -> term.getSchool().getId().equals(teacher.getSchool().getId()))
                .collect(Collectors.toList());
        model.addAttribute("academicTerms", academicTerms);
        
        return "grades/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createGrade(@ModelAttribute GradeDto gradeDto, 
                            BindingResult bindingResult, 
                            RedirectAttributes redirectAttributes,
                            Authentication authentication) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid data provided");
            return "redirect:/grades/create";
        }

        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if teacher can manage this student
            if (!gradeService.canTeacherManageStudent(currentUser.getId(), gradeDto.getStudentId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You cannot assign grades to this student");
                return "redirect:/grades/create";
            }
            
            Grade grade = convertDtoToEntity(gradeDto);
            grade.setTeacher(teacherService.getTeacherById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found")));
            
            if (grade.getDateAwarded() == null) {
                grade.setDateAwarded(LocalDate.now());
            }
            
            gradeService.createGrade(grade);
            redirectAttributes.addFlashAttribute("successMessage", "Grade created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating grade: " + e.getMessage());
        }

        return "redirect:/grades";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String editGradeForm(@PathVariable Long id, Model model, Authentication authentication) {
        Grade grade = gradeService.getGradeById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if this is the teacher's grade
        if (!grade.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        GradeDto gradeDto = convertEntityToDto(grade);
        model.addAttribute("gradeDto", gradeDto);
        
        // Get students that this teacher can manage
        List<Student> students = gradeService.getStudentsForTeacher(currentUser.getId());
        model.addAttribute("students", students);
        
        // Get teacher's qualified subjects
        Teacher teacher = teacherService.getTeacherById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        model.addAttribute("subjects", teacher.getQualifiedSubjects());
        
        // Get academic terms for teacher's school
        List<AcademicTerm> academicTerms = academicTermService.getAllAcademicTerms()
                .stream()
                .filter(term -> term.getSchool().getId().equals(teacher.getSchool().getId()))
                .collect(Collectors.toList());
        model.addAttribute("academicTerms", academicTerms);
        
        return "grades/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateGrade(@PathVariable Long id, 
                            @ModelAttribute GradeDto gradeDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Authentication authentication) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid data provided");
            return "redirect:/grades/edit/" + id;
        }

        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Grade existingGrade = gradeService.getGradeById(id)
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            
            // Check if this is the teacher's grade
            if (!existingGrade.getTeacher().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
                return "redirect:/grades";
            }
            
            gradeDto.setId(id);
            Grade grade = convertDtoToEntity(gradeDto);
            grade.setTeacher(existingGrade.getTeacher()); // Keep original teacher
            
            gradeService.updateGrade(id, grade);
            redirectAttributes.addFlashAttribute("successMessage", "Grade updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating grade: " + e.getMessage());
        }

        return "redirect:/grades";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteGrade(@PathVariable Long id, RedirectAttributes redirectAttributes,
                            Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Grade existingGrade = gradeService.getGradeById(id)
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            
            // Check if this is the teacher's grade
            if (!existingGrade.getTeacher().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
                return "redirect:/grades";
            }
            
            gradeService.deleteGrade(id);
            redirectAttributes.addFlashAttribute("successMessage", "Grade deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting grade: " + e.getMessage());
        }
        return "redirect:/grades";
    }

    private GradeDto convertEntityToDto(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .value(grade.getValue())
                .dateAwarded(grade.getDateAwarded())
                .studentId(grade.getStudent().getId())
                .subjectId(grade.getSubject().getId())
                .teacherId(grade.getTeacher().getId())
                .termId(grade.getTerm() != null ? grade.getTerm().getId() : null)
                .build();
    }

    private Grade convertDtoToEntity(GradeDto dto) {
        Grade grade = Grade.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .dateAwarded(dto.getDateAwarded())
                .build();

        // Set student
        if (dto.getStudentId() != null) {
            Student student = studentService.getStudentById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            grade.setStudent(student);
        }

        // Set subject
        if (dto.getSubjectId() != null) {
            Subject subject = subjectService.getSubjectById(dto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            grade.setSubject(subject);
        }

        // Set academic term
        if (dto.getTermId() != null) {
            AcademicTerm term = academicTermService.getAcademicTermById(dto.getTermId())
                    .orElseThrow(() -> new RuntimeException("Academic term not found"));
            grade.setTerm(term);
        }

        return grade;
    }
} 