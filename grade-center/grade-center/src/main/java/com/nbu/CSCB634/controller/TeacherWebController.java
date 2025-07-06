package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.TeacherDto;
import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.SubjectService;
import com.nbu.CSCB634.service.TeacherService;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherWebController {

    private final TeacherService teacherService;
    private final SchoolService schoolService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String listTeachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        List<TeacherDto> teacherDtos = new ArrayList<>();
        
        for (Teacher teacher : teachers) {
            TeacherDto dto = convertToDto(teacher);
            teacherDtos.add(dto);
        }
        
        model.addAttribute("teachers", teacherDtos);
        return "teachers/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("teacher", new TeacherDto());
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("isEdit", false);
        return "teachers/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String createTeacher(@Valid @ModelAttribute("teacher") TeacherDto teacherDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("schools", schoolService.getAllSchools());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            model.addAttribute("isEdit", false);
            return "teachers/form";
        }

        try {
            // Проверка дали потребителското име вече съществува
            if (userService.findByUsername(teacherDto.getUsername()).isPresent()) {
                result.rejectValue("username", "error.username", "Потребителското име вече съществува");
                model.addAttribute("schools", schoolService.getAllSchools());
                model.addAttribute("subjects", subjectService.getAllSubjects());
                model.addAttribute("isEdit", false);
                return "teachers/form";
            }

            // Създаване на потребител
            User user = User.builder()
                    .username(teacherDto.getUsername())
                    .password(passwordEncoder.encode("teacher123")) // Временна парола
                    .email(teacherDto.getEmail())
                    .firstName(teacherDto.getFirstName())
                    .lastName(teacherDto.getLastName())
                    .role(Role.TEACHER)
                    .build();

            User savedUser;
            try {
                savedUser = userService.save(user);
            } catch (Exception e) {
                // Ако има проблем с sequence, опитай да намериш следващото свободно ID
                if (e.getMessage().contains("duplicate key value violates unique constraint")) {
                    Long nextId = userService.getNextAvailableId();
                    user.setId(nextId);
                    savedUser = userService.save(user);
                } else {
                    throw e;
                }
            }

            // Получаване на квалифицираните предмети
            Set<Subject> qualifiedSubjects = new HashSet<>();
            if (teacherDto.getQualifiedSubjectIds() != null) {
                for (Long subjectId : teacherDto.getQualifiedSubjectIds()) {
                    subjectService.getSubjectById(subjectId).ifPresent(qualifiedSubjects::add);
                }
            }

            // Създаване на учител
            Teacher teacher = Teacher.builder()
                    .user(savedUser)
                    .school(schoolService.getSchoolById(teacherDto.getSchoolId()).orElseThrow())
                    .qualifiedSubjects(qualifiedSubjects)
                    .build();

            teacherService.createTeacher(teacher);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Учителят " + teacherDto.getFullName() + " беше успешно създаден!");
            return "redirect:/teachers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при създаване на учителя: " + e.getMessage());
            return "redirect:/teachers/new";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Teacher> teacherOpt = teacherService.getTeacherById(id);
        if (teacherOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Учителят не беше намерен!");
            return "redirect:/teachers";
        }

        Teacher teacher = teacherOpt.get();
        TeacherDto teacherDto = convertToDto(teacher);
        
        model.addAttribute("teacher", teacherDto);
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("isEdit", true);
        return "teachers/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String updateTeacher(@PathVariable Long id,
                               @Valid @ModelAttribute("teacher") TeacherDto teacherDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("schools", schoolService.getAllSchools());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            model.addAttribute("isEdit", true);
            return "teachers/form";
        }

        try {
            Optional<Teacher> existingTeacherOpt = teacherService.getTeacherById(id);
            if (existingTeacherOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Учителят не беше намерен!");
                return "redirect:/teachers";
            }

            Teacher existingTeacher = existingTeacherOpt.get();
            
            // Проверка дали потребителското име вече съществува при друг потребител
            Optional<User> userWithSameUsername = userService.findByUsername(teacherDto.getUsername());
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(existingTeacher.getUser().getId())) {
                result.rejectValue("username", "error.username", "Потребителското име вече съществува");
                model.addAttribute("schools", schoolService.getAllSchools());
                model.addAttribute("subjects", subjectService.getAllSubjects());
                model.addAttribute("isEdit", true);
                return "teachers/form";
            }

            // Обновяване на потребителските данни
            User user = existingTeacher.getUser();
            user.setUsername(teacherDto.getUsername());
            user.setEmail(teacherDto.getEmail());
            user.setFirstName(teacherDto.getFirstName());
            user.setLastName(teacherDto.getLastName());
            userService.save(user);

            // Получаване на квалифицираните предмети
            Set<Subject> qualifiedSubjects = new HashSet<>();
            if (teacherDto.getQualifiedSubjectIds() != null) {
                for (Long subjectId : teacherDto.getQualifiedSubjectIds()) {
                    subjectService.getSubjectById(subjectId).ifPresent(qualifiedSubjects::add);
                }
            }

            // Обновяване на учителя
            Teacher updatedTeacher = Teacher.builder()
                    .id(existingTeacher.getId())
                    .user(user)
                    .school(schoolService.getSchoolById(teacherDto.getSchoolId()).orElseThrow())
                    .qualifiedSubjects(qualifiedSubjects)
                    .build();

            teacherService.updateTeacher(id, updatedTeacher);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Учителят " + teacherDto.getFullName() + " беше успешно обновен!");
            return "redirect:/teachers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при обновяване на учителя: " + e.getMessage());
            return "redirect:/teachers/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Teacher> teacherOpt = teacherService.getTeacherById(id);
            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                String teacherName = teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName();
                
                teacherService.deleteTeacher(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Учителят " + teacherName + " беше успешно изтрит!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Учителят не беше намерен!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при изтриване на учителя: " + e.getMessage());
        }
        return "redirect:/teachers";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String viewTeacher(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Teacher> teacherOpt = teacherService.getTeacherById(id);
        if (teacherOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Учителят не беше намерен!");
            return "redirect:/teachers";
        }

        Teacher teacher = teacherOpt.get();
        TeacherDto teacherDto = convertToDto(teacher);
        
        model.addAttribute("teacher", teacherDto);
        return "teachers/view";
    }

    // Помощни методи
    private TeacherDto convertToDto(Teacher teacher) {
        Set<Long> qualifiedSubjectIds = teacher.getQualifiedSubjects() != null ? 
                teacher.getQualifiedSubjects().stream()
                        .map(Subject::getId)
                        .collect(Collectors.toSet()) : new HashSet<>();

        List<String> qualifiedSubjectNames = teacher.getQualifiedSubjects() != null ?
                teacher.getQualifiedSubjects().stream()
                        .map(Subject::getName)
                        .sorted()
                        .collect(Collectors.toList()) : new ArrayList<>();

        return TeacherDto.builder()
                .id(teacher.getId())
                .username(teacher.getUser().getUsername())
                .firstName(teacher.getUser().getFirstName())
                .lastName(teacher.getUser().getLastName())
                .email(teacher.getUser().getEmail())
                .schoolId(teacher.getSchool().getId())
                .schoolName(teacher.getSchool().getName())
                .qualifiedSubjectIds(qualifiedSubjectIds)
                .qualifiedSubjectNames(qualifiedSubjectNames)
                .build();
    }
} 