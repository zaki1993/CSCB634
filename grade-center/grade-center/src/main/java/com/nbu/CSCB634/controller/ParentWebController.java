package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.ParentDto;
import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.ParentService;
import com.nbu.CSCB634.service.StudentService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parents")
@RequiredArgsConstructor
public class ParentWebController {
    
    private final ParentService parentService;
    private final UserService userService;
    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String listParents(Model model) {
        List<Parent> parents = parentService.getAllParents();
        List<ParentDto> parentDtos = parents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("parents", parentDtos);
        return "parents/list";
    }
    
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String createParentForm(Model model) {
        model.addAttribute("parent", new ParentDto());
        model.addAttribute("students", studentService.getAllStudents());
        return "parents/form";
    }
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String createParent(@Valid @ModelAttribute("parent") ParentDto parentDto, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            return "parents/form";
        }
        
        try {
            // Създаване на User
            User user = User.builder()
                    .username(parentDto.getUsername())
                    .firstName(parentDto.getFirstName())
                    .lastName(parentDto.getLastName())
                    .email(parentDto.getEmail())
                    .password(passwordEncoder.encode("parent123")) // Парола по подразбиране
                    .role(Role.PARENT)
                    .build();
            
            User savedUser;
//            try {
//                savedUser = userService.registerUser(user);
//            } catch (Exception e) {
//                // Опитваме с next available ID
//                Long nextId = userService.getNextAvailableId();
//                user.setId(nextId);
//                savedUser = userService.registerUser(user);
//            }
            
            // Създаване на Parent
            Parent parent = Parent.builder()
                    .id(user.getId())
                    .user(user)
                    .build();
            
            // Задаване на ученици
            if (parentDto.getStudentIds() != null && !parentDto.getStudentIds().isEmpty()) {
                Set<Student> students = new HashSet<>();
                for (Long studentId : parentDto.getStudentIds()) {
                    studentService.getStudentById(studentId).ifPresent(students::add);
                }
                parent.setStudents(students);
            }
            
            Parent savedParent;
            try {
                savedParent = parentService.createParent(parent);
            } catch (Exception e) {
                // Опитваме с next available ID
                Long nextId = parentService.getNextAvailableId();
                parent.setId(nextId);
                savedParent = parentService.createParent(parent);
            }
            
            redirectAttributes.addFlashAttribute("success", "Родителят е създаден успешно!");
            return "redirect:/parents";
            
        } catch (Exception e) {
            result.rejectValue("username", "error.general", "Грешка при създаване на родител: " + e.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            return "parents/form";
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String viewParent(@PathVariable Long id, Model model) {
        Parent parent = parentService.getParentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Родителят не е намерен"));
        
        ParentDto parentDto = convertToDto(parent);
        model.addAttribute("parent", parentDto);
        return "parents/view";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String editParentForm(@PathVariable Long id, Model model) {
        Parent parent = parentService.getParentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Родителят не е намерен"));
        
        ParentDto parentDto = convertToDto(parent);
        model.addAttribute("parent", parentDto);
        model.addAttribute("students", studentService.getAllStudents());
        return "parents/form";
    }
    
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String updateParent(@PathVariable Long id, 
                              @Valid @ModelAttribute("parent") ParentDto parentDto, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            parentDto.setId(id);
            model.addAttribute("students", studentService.getAllStudents());
            return "parents/form";
        }
        
        try {
            Parent parent = parentService.getParentById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Родителят не е намерен"));
            
            // Обновяване на User данните
            parent.getUser().setFirstName(parentDto.getFirstName());
            parent.getUser().setLastName(parentDto.getLastName());
            parent.getUser().setEmail(parentDto.getEmail());
            
            // Обновяване на ученици
            if (parentDto.getStudentIds() != null && !parentDto.getStudentIds().isEmpty()) {
                Set<Student> students = new HashSet<>();
                for (Long studentId : parentDto.getStudentIds()) {
                    studentService.getStudentById(studentId).ifPresent(students::add);
                }
                parent.setStudents(students);
            } else {
                parent.setStudents(new HashSet<>());
            }
            
            parentService.updateParent(id, parent);
            redirectAttributes.addFlashAttribute("success", "Данните за родителя са обновени успешно!");
            return "redirect:/parents";
            
        } catch (Exception e) {
            result.rejectValue("firstName", "error.general", "Грешка при обновяване: " + e.getMessage());
            parentDto.setId(id);
            model.addAttribute("students", studentService.getAllStudents());
            return "parents/form";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteParent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            parentService.deleteParent(id);
            redirectAttributes.addFlashAttribute("success", "Родителят е изтрит успешно!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Грешка при изтриване: " + e.getMessage());
        }
        return "redirect:/parents";
    }
    
    private ParentDto convertToDto(Parent parent) {
        ParentDto dto = ParentDto.builder()
                .id(parent.getId())
                .username(parent.getUser().getUsername())
                .firstName(parent.getUser().getFirstName())
                .lastName(parent.getUser().getLastName())
                .email(parent.getUser().getEmail())
                .build();
        
        if (parent.getStudents() != null) {
            dto.setStudentIds(parent.getStudents().stream()
                    .map(Student::getId)
                    .collect(Collectors.toSet()));
            dto.setStudentNames(parent.getStudents().stream()
                    .map(student -> student.getUser().getFirstName() + " " + student.getUser().getLastName())
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 