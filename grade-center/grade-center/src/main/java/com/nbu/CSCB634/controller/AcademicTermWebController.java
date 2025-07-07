package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.AcademicTermDto;
import com.nbu.CSCB634.model.AcademicTerm;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.service.AcademicTermService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.SubjectService;
import com.nbu.CSCB634.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/academic-terms")
@RequiredArgsConstructor
public class AcademicTermWebController {

    private final AcademicTermService academicTermService;
    private final SchoolService schoolService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('DIRECTOR') or hasRole('TEACHER')")
    public String listAcademicTerms(Model model) {
        List<AcademicTerm> academicTerms = academicTermService.getAllAcademicTerms();
        model.addAttribute("academicTerms", academicTerms);
        return "academic-terms/list";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('DIRECTOR') or hasRole('TEACHER')")
    public String viewAcademicTerm(@PathVariable Long id, Model model) {
        AcademicTerm academicTerm = academicTermService.getAcademicTermById(id)
                .orElseThrow(() -> new RuntimeException("Academic term not found"));
        model.addAttribute("academicTerm", academicTerm);
        return "academic-terms/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('DIRECTOR')")
    public String createAcademicTermForm(Model model) {
        model.addAttribute("academicTermDto", new AcademicTermDto());
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "academic-terms/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('DIRECTOR')")
    public String createAcademicTerm(@ModelAttribute AcademicTermDto academicTermDto, 
                                   BindingResult bindingResult, 
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid data provided");
            return "redirect:/academic-terms/create";
        }

        try {
            AcademicTerm academicTerm = convertDtoToEntity(academicTermDto);
            academicTermService.createAcademicTerm(academicTerm);
            redirectAttributes.addFlashAttribute("successMessage", "Academic term created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating academic term: " + e.getMessage());
        }

        return "redirect:/academic-terms";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('DIRECTOR')")
    public String editAcademicTermForm(@PathVariable Long id, Model model) {
        AcademicTerm academicTerm = academicTermService.getAcademicTermById(id)
                .orElseThrow(() -> new RuntimeException("Academic term not found"));
        
        AcademicTermDto academicTermDto = convertEntityToDto(academicTerm);
        model.addAttribute("academicTermDto", academicTermDto);
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "academic-terms/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('DIRECTOR')")
    public String updateAcademicTerm(@PathVariable Long id, 
                                   @ModelAttribute AcademicTermDto academicTermDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid data provided");
            return "redirect:/academic-terms/edit/" + id;
        }

        try {
            academicTermDto.setId(id);
            AcademicTerm academicTerm = convertDtoToEntity(academicTermDto);
            academicTermService.updateAcademicTerm(id, academicTerm);
            redirectAttributes.addFlashAttribute("successMessage", "Academic term updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating academic term: " + e.getMessage());
        }

        return "redirect:/academic-terms";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteAcademicTerm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            academicTermService.deleteAcademicTerm(id);
            redirectAttributes.addFlashAttribute("successMessage", "Academic term deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting academic term: " + e.getMessage());
        }
        return "redirect:/academic-terms";
    }

    private AcademicTermDto convertEntityToDto(AcademicTerm academicTerm) {
        return AcademicTermDto.builder()
                .id(academicTerm.getId())
                .name(academicTerm.getName())
                .startDate(academicTerm.getStartDate())
                .endDate(academicTerm.getEndDate())
                .schoolId(academicTerm.getSchool() != null ? academicTerm.getSchool().getId() : null)
                .subjectIds(academicTerm.getSubjects() != null ? 
                    academicTerm.getSubjects().stream()
                        .map(Subject::getId)
                        .collect(Collectors.toSet()) : new HashSet<>())
                .teacherIds(academicTerm.getTeachers() != null ? 
                    academicTerm.getTeachers().stream()
                        .map(Teacher::getId)
                        .collect(Collectors.toSet()) : new HashSet<>())
                .build();
    }

    private AcademicTerm convertDtoToEntity(AcademicTermDto dto) {
        AcademicTerm academicTerm = AcademicTerm.builder()
                .id(dto.getId())
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        // Set school
        if (dto.getSchoolId() != null) {
            School school = schoolService.getSchoolById(dto.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
            academicTerm.setSchool(school);
        }

        // Set subjects
        if (dto.getSubjectIds() != null && !dto.getSubjectIds().isEmpty()) {
            Set<Subject> subjects = dto.getSubjectIds().stream()
                    .map(subjectId -> subjectService.getSubjectById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Subject not found")))
                    .collect(Collectors.toSet());
            academicTerm.setSubjects(subjects);
        }

        // Set teachers
        if (dto.getTeacherIds() != null && !dto.getTeacherIds().isEmpty()) {
            Set<Teacher> teachers = dto.getTeacherIds().stream()
                    .map(teacherId -> teacherService.getTeacherById(teacherId)
                            .orElseThrow(() -> new RuntimeException("Teacher not found")))
                    .collect(Collectors.toSet());
            academicTerm.setTeachers(teachers);
        }

        return academicTerm;
    }

    private Long getNextAvailableId() {
        List<AcademicTerm> allAcademicTerms = academicTermService.getAllAcademicTerms();
        return allAcademicTerms.stream()
                .mapToLong(AcademicTerm::getId)
                .max()
                .orElse(0L) + 1;
    }
} 