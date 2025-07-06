package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.service.SchoolService;
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
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolWebController {

    private final SchoolService schoolService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public String listSchools(Model model) {
        List<School> schools = schoolService.getAllSchools();
        model.addAttribute("schools", schools);
        return "schools/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("school", new School());
        model.addAttribute("isEdit", false);
        return "schools/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String createSchool(@Valid @ModelAttribute School school, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "schools/form";
        }
        
        try {
            schoolService.createSchool(school);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Училището \"" + school.getName() + "\" беше успешно създадено!");
            return "redirect:/schools";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Грешка при създаване на училището: " + e.getMessage());
            return "redirect:/schools/new";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<School> schoolOpt = schoolService.getSchoolById(id);
        if (schoolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Училището не беше намерено!");
            return "redirect:/schools";
        }
        
        model.addAttribute("school", schoolOpt.get());
        model.addAttribute("isEdit", true);
        return "schools/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String updateSchool(@PathVariable Long id, 
                              @Valid @ModelAttribute School school, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "schools/form";
        }
        
        try {
            schoolService.updateSchool(id, school);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Училището \"" + school.getName() + "\" беше успешно обновено!");
            return "redirect:/schools";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Училището не беше намерено!");
            return "redirect:/schools";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Грешка при обновяване на училището: " + e.getMessage());
            return "redirect:/schools/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteSchool(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<School> schoolOpt = schoolService.getSchoolById(id);
            if (schoolOpt.isPresent()) {
                String schoolName = schoolOpt.get().getName();
                schoolService.deleteSchool(id);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Училището \"" + schoolName + "\" беше успешно изтрито!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Училището не беше намерено!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Грешка при изтриване на училището: " + e.getMessage());
        }
        return "redirect:/schools";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public String viewSchool(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<School> schoolOpt = schoolService.getSchoolById(id);
        if (schoolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Училището не беше намерено!");
            return "redirect:/schools";
        }
        
        model.addAttribute("school", schoolOpt.get());
        return "schools/view";
    }
} 