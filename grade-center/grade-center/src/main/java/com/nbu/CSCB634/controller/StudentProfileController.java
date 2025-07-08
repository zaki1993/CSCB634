package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.StudentDto;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.StudentService;
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

@Controller
@RequestMapping("/student/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentService studentService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public String showProfile(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
            if (studentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ученикът не беше намерен!");
                return "redirect:/";
            }

            Student student = studentOpt.get();
            StudentDto studentDto = convertToDto(student);
            
            model.addAttribute("student", studentDto);
            model.addAttribute("isEdit", false);
            return "student/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при зареждане на профила: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/edit")
    @PreAuthorize("hasRole('STUDENT')")
    public String showEditForm(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
            if (studentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ученикът не беше намерен!");
                return "redirect:/";
            }

            Student student = studentOpt.get();
            StudentDto studentDto = convertToDto(student);
            
            model.addAttribute("student", studentDto);
            model.addAttribute("isEdit", true);
            return "student/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при зареждане на профила: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('STUDENT')")
    public String updateProfile(@Valid @ModelAttribute("student") StudentDto studentDto,
                               BindingResult result,
                               Model model,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "student/profile";
        }

        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            
            // Проверка дали имейлът вече съществува при друг потребител
            Optional<User> userWithSameEmail = userService.findByEmail(studentDto.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
                result.rejectValue("email", "error.email", "Имейлът вече съществува");
                model.addAttribute("isEdit", true);
                return "student/profile";
            }

            // Обновяване на потребителските данни директно чрез UserService
            user.setFirstName(studentDto.getFirstName());
            user.setLastName(studentDto.getLastName());
            user.setEmail(studentDto.getEmail());
            userService.save(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Профилът беше успешно обновен!");
            return "redirect:/student/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при обновяване на профила: " + e.getMessage());
            return "redirect:/student/profile/edit";
        }
    }

    // Helper method to convert Student entity to DTO
    private StudentDto convertToDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .username(student.getUser().getUsername())
                .firstName(student.getUser().getFirstName())
                .lastName(student.getUser().getLastName())
                .email(student.getUser().getEmail())
                .schoolId(student.getSchool().getId())
                .schoolName(student.getSchool().getName())
                .schoolClassId(student.getSchoolClass() != null ? student.getSchoolClass().getId() : null)
                .schoolClassName(student.getSchoolClass() != null ? student.getSchoolClass().getName() : null)
                .build();
    }
} 