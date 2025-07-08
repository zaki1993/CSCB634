package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.TeacherDto;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.TeacherService;
import com.nbu.CSCB634.service.auth.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher/profile")
@RequiredArgsConstructor
public class TeacherProfileController {

    private final TeacherService teacherService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public String showProfile(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(user.getId());
            if (teacherOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Учителят не беше намерен!");
                return "redirect:/";
            }

            Teacher teacher = teacherOpt.get();
            TeacherDto teacherDto = convertToDto(teacher);
            
            model.addAttribute("teacher", teacherDto);
            model.addAttribute("isEdit", false);
            return "teacher/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при зареждане на профила: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(user.getId());
            if (teacherOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Учителят не беше намерен!");
                return "redirect:/";
            }

            Teacher teacher = teacherOpt.get();
            TeacherDto teacherDto = convertToDto(teacher);
            
            model.addAttribute("teacher", teacherDto);
            model.addAttribute("isEdit", true);
            return "teacher/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при зареждане на профила: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateProfile(@Valid @ModelAttribute("teacher") TeacherDto teacherDto,
                               BindingResult result,
                               Model model,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "teacher/profile";
        }

        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            
            // Проверка дали имейлът вече съществува при друг потребител
            Optional<User> userWithSameEmail = userService.findByEmail(teacherDto.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
                result.rejectValue("email", "error.email", "Имейлът вече съществува");
                model.addAttribute("isEdit", true);
                return "teacher/profile";
            }

            // Обновяване на потребителските данни директно чрез UserService
            user.setFirstName(teacherDto.getFirstName());
            user.setLastName(teacherDto.getLastName());
            user.setEmail(teacherDto.getEmail());
            userService.save(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Профилът беше успешно обновен!");
            return "redirect:/teacher/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при обновяване на профила: " + e.getMessage());
            return "redirect:/teacher/profile/edit";
        }
    }

    // Helper method to convert Teacher entity to DTO
    private TeacherDto convertToDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .username(teacher.getUser().getUsername())
                .firstName(teacher.getUser().getFirstName())
                .lastName(teacher.getUser().getLastName())
                .email(teacher.getUser().getEmail())
                .schoolId(teacher.getSchool().getId())
                .schoolName(teacher.getSchool().getName())
                .qualifiedSubjectIds(teacher.getQualifiedSubjects() != null ?
                    teacher.getQualifiedSubjects().stream().map(s -> s.getId()).collect(Collectors.toSet()) : null)
                .qualifiedSubjectNames(teacher.getQualifiedSubjects() != null ?
                    teacher.getQualifiedSubjects().stream().map(s -> s.getName()).collect(Collectors.toList()) : null)
                .build();
    }
} 