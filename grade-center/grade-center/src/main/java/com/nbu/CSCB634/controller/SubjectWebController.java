package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.SubjectDto;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectWebController {

    private final SubjectService subjectService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String listSubjects(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "subjects/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("subject", new SubjectDto());
        model.addAttribute("isEdit", false);
        return "subjects/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String createSubject(@Valid @ModelAttribute("subject") SubjectDto subjectDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "subjects/form";
        }

        try {
            Subject subject = Subject.builder()
                    .name(subjectDto.getName())
                    .build();

            subjectService.createSubject(subject);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Предметът \"" + subjectDto.getName() + "\" беше успешно създаден!");
            return "redirect:/subjects";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при създаване на предмета: " + e.getMessage());
            return "redirect:/subjects/new";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Предметът не беше намерен!");
            return "redirect:/subjects";
        }

        Subject subject = subjectOpt.get();
        SubjectDto subjectDto = convertToDto(subject);
        
        model.addAttribute("subject", subjectDto);
        model.addAttribute("isEdit", true);
        return "subjects/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String updateSubject(@PathVariable Long id,
                               @Valid @ModelAttribute("subject") SubjectDto subjectDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "subjects/form";
        }

        try {
            Subject subject = Subject.builder()
                    .id(id)
                    .name(subjectDto.getName())
                    .build();

            subjectService.updateSubject(id, subject);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Предметът \"" + subjectDto.getName() + "\" беше успешно обновен!");
            return "redirect:/subjects";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Предметът не беше намерен!");
            return "redirect:/subjects";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при обновяване на предмета: " + e.getMessage());
            return "redirect:/subjects/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
            if (subjectOpt.isPresent()) {
                String subjectName = subjectOpt.get().getName();
                subjectService.deleteSubject(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Предметът \"" + subjectName + "\" беше успешно изтрит!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Предметът не беше намерен!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при изтриване на предмета: " + e.getMessage());
        }
        return "redirect:/subjects";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String viewSubject(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Предметът не беше намерен!");
            return "redirect:/subjects";
        }

        Subject subject = subjectOpt.get();
        SubjectDto subjectDto = convertToDto(subject);
        
        model.addAttribute("subject", subjectDto);
        return "subjects/view";
    }

    // Helper method to convert Subject entity to DTO
    private SubjectDto convertToDto(Subject subject) {
        return SubjectDto.builder()
                .id(subject.getId())
                .name(subject.getName())
                .build();
    }
} 