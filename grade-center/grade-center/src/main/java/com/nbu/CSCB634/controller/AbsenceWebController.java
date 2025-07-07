package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.AbsenceDto;
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

import java.util.List;

@Controller
@RequestMapping("/absences")
@RequiredArgsConstructor
public class AbsenceWebController {

    private final AbsenceService absenceService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String listAbsences(Model model, Authentication authentication) {
        List<Absence> absences;
        
        if (AccessControlConfig.isTeacher()) {
            // Teacher sees only their absences
            User currentUser = userService.findByUsername(authentication.getName())
                                          .orElseThrow(() -> new RuntimeException("User not found"));
            absences = absenceService.getAbsencesByTeacherId(currentUser.getId());
        } else {
            // Admin/Director sees all absences
            absences = absenceService.getAll();
        }
        
        model.addAttribute("absences", absences);
        return "absences/list";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String viewAbsence(@PathVariable Long id, Model model, Authentication authentication) {
        Absence absence = absenceService.getAbsenceById(id)
                .orElseThrow(() -> new RuntimeException("Absence not found"));
        
        // Check if teacher can view this absence
        if (AccessControlConfig.isTeacher()) {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!absence.getTeacher().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied");
            }
        }
        
        model.addAttribute("absence", absence);
        return "absences/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createAbsenceForm(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("absenceDto", new AbsenceDto());
        
        // Get students that this teacher can manage
        List<Student> students = absenceService.getStudentsForTeacher(currentUser.getId());
        model.addAttribute("students", students);
        
        // Get teacher's qualified subjects
        Teacher teacher = teacherService.getTeacherById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        model.addAttribute("subjects", teacher.getQualifiedSubjects());
        
        return "absences/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createAbsence(@ModelAttribute AbsenceDto absenceDto, 
                              BindingResult bindingResult, 
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid data provided");
            return "redirect:/absences/create";
        }

        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if teacher can manage this student
            if (!absenceService.canTeacherManageStudent(currentUser.getId(), absenceDto.getStudentId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You cannot register absences for this student");
                return "redirect:/absences/create";
            }
            
            Absence absence = convertDtoToEntity(absenceDto);
            absence.setTeacher(teacherService.getTeacherById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found")));
            
            absenceService.createAbsence(absence);
            redirectAttributes.addFlashAttribute("successMessage", "Absence registered successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering absence: " + e.getMessage());
        }

        return "redirect:/absences";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String editAbsenceForm(@PathVariable Long id, Model model, Authentication authentication) {
        Absence absence = absenceService.getAbsenceById(id)
                .orElseThrow(() -> new RuntimeException("Absence not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if this is the teacher's absence
        if (!absence.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        AbsenceDto absenceDto = convertEntityToDto(absence);
        model.addAttribute("absenceDto", absenceDto);
        
        // Get students that this teacher can manage
        List<Student> students = absenceService.getStudentsForTeacher(currentUser.getId());
        model.addAttribute("students", students);
        
        // Get teacher's qualified subjects
        Teacher teacher = teacherService.getTeacherById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        model.addAttribute("subjects", teacher.getQualifiedSubjects());
        
        return "absences/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateAbsence(@PathVariable Long id, 
                              @ModelAttribute AbsenceDto absenceDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid data provided");
            return "redirect:/absences/edit/" + id;
        }

        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Absence existingAbsence = absenceService.getAbsenceById(id)
                    .orElseThrow(() -> new RuntimeException("Absence not found"));
            
            // Check if this is the teacher's absence
            if (!existingAbsence.getTeacher().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
                return "redirect:/absences";
            }
            
            absenceDto.setId(id);
            Absence absence = convertDtoToEntity(absenceDto);
            absence.setTeacher(existingAbsence.getTeacher()); // Keep original teacher
            
            absenceService.updateAbsence(id, absence);
            redirectAttributes.addFlashAttribute("successMessage", "Absence updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating absence: " + e.getMessage());
        }

        return "redirect:/absences";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteAbsence(@PathVariable Long id, RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Absence existingAbsence = absenceService.getAbsenceById(id)
                    .orElseThrow(() -> new RuntimeException("Absence not found"));
            
            // Check if this is the teacher's absence
            if (!existingAbsence.getTeacher().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
                return "redirect:/absences";
            }
            
            absenceService.deleteAbsence(id);
            redirectAttributes.addFlashAttribute("successMessage", "Absence deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting absence: " + e.getMessage());
        }

        return "redirect:/absences";
    }

    private AbsenceDto convertEntityToDto(Absence absence) {
        AbsenceDto dto = new AbsenceDto();
        dto.setId(absence.getId());
        dto.setAbsenceDate(absence.getAbsenceDate());
        dto.setJustified(absence.getJustified());
        dto.setStudentId(absence.getStudent().getId());
        dto.setSubjectId(absence.getSubject() != null ? absence.getSubject().getId() : null);
        return dto;
    }

    private Absence convertDtoToEntity(AbsenceDto dto) {
        Absence absence = new Absence();
        absence.setAbsenceDate(dto.getAbsenceDate());
        absence.setJustified(dto.getJustified());
        
        // Set student
        Student student = studentService.getStudentById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        absence.setStudent(student);
        
        // Set subject (optional)
        if (dto.getSubjectId() != null) {
            Subject subject = subjectService.getSubjectById(dto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            absence.setSubject(subject);
        }
        
        return absence;
    }
} 