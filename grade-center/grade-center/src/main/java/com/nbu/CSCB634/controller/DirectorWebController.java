package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.DirectorDto;
import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.DirectorService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.auth.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorWebController {
    
    private final DirectorService directorService;
    private final UserService userService;
    private final SchoolService schoolService;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String listDirectors(Model model) {
        List<Director> directors = directorService.getAllDirectors();
        List<DirectorDto> directorDtos = directors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("directors", directorDtos);
        return "directors/list";
    }
    
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String createDirectorForm(Model model) {
        model.addAttribute("director", new DirectorDto());
        model.addAttribute("schools", schoolService.getAllSchools());
        return "directors/form";
    }
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String createDirector(@Valid @ModelAttribute("director") DirectorDto directorDto, 
                                BindingResult result, 
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("schools", schoolService.getAllSchools());
            return "directors/form";
        }
        
        try {
            
            // Създаване на User с уникално име ако е необходимо
            String baseUsername = directorDto.getUsername().trim().toLowerCase();
            String uniqueUsername = baseUsername;
            int counter = 1;
            
            // Намираме уникално потребителско име
            while (userService.findByUsername(uniqueUsername).isPresent()) {
                uniqueUsername = baseUsername + counter;
                counter++;
            }
            
            // Проверка за email конфликт
            if (userService.existsByEmail(directorDto.getEmail().trim())) {
                result.rejectValue("email", "error.email", "Email адресът вече се използва");
                model.addAttribute("schools", schoolService.getAllSchools());
                return "directors/form";
            }
            
            User user = User.builder()
                    .username(uniqueUsername)
                    .firstName(directorDto.getFirstName().trim())
                    .lastName(directorDto.getLastName().trim())
                    .email(directorDto.getEmail().trim())
                    .password(passwordEncoder.encode("director123")) // Парола по подразбиране
                    .role(Role.DIRECTOR)
                    .build();
            
            User savedUser;
//            try {
//                savedUser = userService.registerUser(user);
//
//                // Проверка дали User-ът е запазен с ID
//                if (savedUser == null || savedUser.getId() == null) {
//                    throw new IllegalArgumentException("Грешка при създаване на потребител - ID е null");
//                }
//            } catch (com.nbu.CSCB634.service.exceptions.UserAlreadyExistException e) {
//                // Ако username или email вече съществуват, не можем да продължим
//                throw new IllegalArgumentException("Потребителското име или email вече се използват: " + e.getMessage());
//            } catch (Exception e) {
//                // Ако има проблем с auto-generation, опитваме с ръчно ID
//                Long nextId = userService.getNextAvailableId();
//                user.setId(nextId);
//                savedUser = userService.save(user);
//
//                if (savedUser == null || savedUser.getId() == null) {
//                    throw new IllegalArgumentException("Грешка при създаване на потребител - ID е все още null");
//                }
//            }
            
            // Намиране на училището
            School school = schoolService.getSchoolById(directorDto.getSchoolId())
                    .orElseThrow(() -> new IllegalArgumentException("Училището не е намерено"));
            
            // Създаване на Director
            Director director = Director.builder()
                    .id(user.getId())
                    .user(user)
                    .school(school)
                    .build();
            
            Director savedDirector = directorService.createDirector(director);
            
            String successMessage = "Директорът е създаден успешно!";
            if (!uniqueUsername.equals(baseUsername)) {
                successMessage += " Потребителското име е променено на '" + uniqueUsername + "' заради конфликт.";
            }
            
            redirectAttributes.addFlashAttribute("success", successMessage);
            return "redirect:/directors";
            
        } catch (Exception e) {
            result.rejectValue("username", "error.general", "Грешка при създаване на директор: " + e.getMessage());
            model.addAttribute("schools", schoolService.getAllSchools());
            return "directors/form";
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String viewDirector(@PathVariable Long id, Model model) {
        Director director = directorService.getDirectorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Директорът не е намерен"));
        
        DirectorDto directorDto = convertToDto(director);
        model.addAttribute("director", directorDto);
        return "directors/view";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String editDirectorForm(@PathVariable Long id, Model model) {
        Director director = directorService.getDirectorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Директорът не е намерен"));
        
        DirectorDto directorDto = convertToDto(director);
        model.addAttribute("director", directorDto);
        model.addAttribute("schools", schoolService.getAllSchools());
        return "directors/form";
    }
    
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String updateDirector(@PathVariable Long id, 
                                @Valid @ModelAttribute("director") DirectorDto directorDto, 
                                BindingResult result, 
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            directorDto.setId(id);
            model.addAttribute("schools", schoolService.getAllSchools());
            return "directors/form";
        }
        
        try {
            Director director = directorService.getDirectorById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Директорът не е намерен"));
            
            // Обновяване на User данните
            director.getUser().setFirstName(directorDto.getFirstName());
            director.getUser().setLastName(directorDto.getLastName());
            director.getUser().setEmail(directorDto.getEmail());
            
            // Обновяване на училището
            School school = schoolService.getSchoolById(directorDto.getSchoolId())
                    .orElseThrow(() -> new IllegalArgumentException("Училището не е намерено"));
            director.setSchool(school);
            
            directorService.updateDirector(id, director);
            redirectAttributes.addFlashAttribute("success", "Данните за директора са обновени успешно!");
            return "redirect:/directors";
            
        } catch (Exception e) {
            result.rejectValue("firstName", "error.general", "Грешка при обновяване: " + e.getMessage());
            directorDto.setId(id);
            model.addAttribute("schools", schoolService.getAllSchools());
            return "directors/form";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteDirector(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            directorService.deleteDirector(id);
            redirectAttributes.addFlashAttribute("success", "Директорът е изтрит успешно!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Грешка при изтриване: " + e.getMessage());
        }
        return "redirect:/directors";
    }
    
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