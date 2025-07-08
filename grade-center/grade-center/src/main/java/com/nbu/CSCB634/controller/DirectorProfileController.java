package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.DirectorDto;
import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.DirectorService;
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
@RequestMapping("/director/profile")
@RequiredArgsConstructor
public class DirectorProfileController {

    private final DirectorService directorService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('DIRECTOR')")
    public String showProfile(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            Optional<Director> directorOpt = directorService.getDirectorByUserId(user.getId());
            if (directorOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Директорът не беше намерен!");
                return "redirect:/";
            }

            Director director = directorOpt.get();
            DirectorDto directorDto = convertToDto(director);
            
            model.addAttribute("director", directorDto);
            model.addAttribute("isEdit", false);
            return "director/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при зареждане на профила: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/edit")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String showEditForm(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            Optional<Director> directorOpt = directorService.getDirectorByUserId(user.getId());
            if (directorOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Директорът не беше намерен!");
                return "redirect:/";
            }

            Director director = directorOpt.get();
            DirectorDto directorDto = convertToDto(director);
            
            model.addAttribute("director", directorDto);
            model.addAttribute("isEdit", true);
            return "director/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при зареждане на профила: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String updateProfile(@Valid @ModelAttribute("director") DirectorDto directorDto,
                               BindingResult result,
                               Model model,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "director/profile";
        }

        try {
            Optional<User> userOpt = userService.findByUsername(principal.getName());
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Потребителят не беше намерен!");
                return "redirect:/";
            }

            User user = userOpt.get();
            
            // Проверка дали имейлът вече съществува при друг потребител
            Optional<User> userWithSameEmail = userService.findByEmail(directorDto.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
                result.rejectValue("email", "error.email", "Имейлът вече съществува");
                model.addAttribute("isEdit", true);
                return "director/profile";
            }

            // Обновяване на потребителските данни директно чрез UserService
            user.setFirstName(directorDto.getFirstName());
            user.setLastName(directorDto.getLastName());
            user.setEmail(directorDto.getEmail());
            userService.save(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Профилът беше успешно обновен!");
            return "redirect:/director/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Грешка при обновяване на профила: " + e.getMessage());
            return "redirect:/director/profile/edit";
        }
    }

    // Helper method to convert Director entity to DTO
    private DirectorDto convertToDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .username(director.getUser().getUsername())
                .firstName(director.getUser().getFirstName())
                .lastName(director.getUser().getLastName())
                .email(director.getUser().getEmail())
                .schoolId(director.getSchool().getId())
                .schoolName(director.getSchool().getName())
                .build();
    }
} 