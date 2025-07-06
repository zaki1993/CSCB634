package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.dto.StudentDto;
import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.SchoolClassService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.StudentService;
import com.nbu.CSCB634.service.auth.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentWebController {

    private final StudentService studentService;
    private final SchoolService schoolService;
    private final SchoolClassService schoolClassService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public String listStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        List<StudentDto> studentDtos = new ArrayList<>();
        
        for (Student student : students) {
            StudentDto dto = convertToDto(student);
            studentDtos.add(dto);
        }
        
        model.addAttribute("students", studentDtos);
        return "students/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentDto());
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("schoolClasses", new ArrayList<SchoolClass>());
        model.addAttribute("isEdit", false);
        return "students/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String createStudent(@Valid @ModelAttribute("student") StudentDto studentDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("schools", schoolService.getAllSchools());
            model.addAttribute("schoolClasses", getSchoolClassesForSchool(studentDto.getSchoolId()));
            model.addAttribute("isEdit", false);
            return "students/form";
        }

        try {
            // Проверка дали потребителското име вече съществува
            if (userService.findByUsername(studentDto.getUsername()).isPresent()) {
                result.rejectValue("username", "error.username", "Потребителското име вече съществува");
                model.addAttribute("schools", schoolService.getAllSchools());
                model.addAttribute("schoolClasses", getSchoolClassesForSchool(studentDto.getSchoolId()));
                model.addAttribute("isEdit", false);
                return "students/form";
            }

            // Създаване на потребител
            User user = User.builder()
                    .username(studentDto.getUsername())
                    .password(passwordEncoder.encode("student123")) // Временна парола
                    .email(studentDto.getEmail())
                    .firstName(studentDto.getFirstName())
                    .lastName(studentDto.getLastName())
                    .role(Role.STUDENT)
                    .build();

            User savedUser;
            try {
                savedUser = userService.save(user);
            } catch (Exception e) {
                // Ако има проблем с sequence, опитай да намериш следващото свободно ID
                if (e.getMessage().contains("duplicate key value violates unique constraint")) {
                    // Временна поправка - намери следващото свободно ID
                    Long nextId = userService.getNextAvailableId();
                    user.setId(nextId);
                    savedUser = userService.save(user);
                } else {
                    throw e;
                }
            }

            // Създаване на ученик
            Student student = Student.builder()
                    .user(savedUser)
                    .school(schoolService.getSchoolById(studentDto.getSchoolId()).orElseThrow())
                    .schoolClass(studentDto.getSchoolClassId() != null ? 
                            schoolClassService.getSchoolClassById(studentDto.getSchoolClassId()).orElse(null) : null)
                    .build();

            studentService.createStudent(student);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ученикът " + studentDto.getFullName() + " беше успешно създаден!");
            return "redirect:/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при създаване на ученика: " + e.getMessage());
            return "redirect:/students/new";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ученикът не беше намерен!");
            return "redirect:/students";
        }

        Student student = studentOpt.get();
        StudentDto studentDto = convertToDto(student);
        
        model.addAttribute("student", studentDto);
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("schoolClasses", getSchoolClassesForSchool(studentDto.getSchoolId()));
        model.addAttribute("isEdit", true);
        return "students/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String updateStudent(@PathVariable Long id,
                               @Valid @ModelAttribute("student") StudentDto studentDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("schools", schoolService.getAllSchools());
            model.addAttribute("schoolClasses", getSchoolClassesForSchool(studentDto.getSchoolId()));
            model.addAttribute("isEdit", true);
            return "students/form";
        }

        try {
            Optional<Student> existingStudentOpt = studentService.getStudentById(id);
            if (existingStudentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ученикът не беше намерен!");
                return "redirect:/students";
            }

            Student existingStudent = existingStudentOpt.get();
            
            // Проверка дали потребителското име вече съществува при друг потребител
            Optional<User> userWithSameUsername = userService.findByUsername(studentDto.getUsername());
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(existingStudent.getUser().getId())) {
                result.rejectValue("username", "error.username", "Потребителското име вече съществува");
                model.addAttribute("schools", schoolService.getAllSchools());
                model.addAttribute("schoolClasses", getSchoolClassesForSchool(studentDto.getSchoolId()));
                model.addAttribute("isEdit", true);
                return "students/form";
            }

            // Обновяване на потребителските данни
            User user = existingStudent.getUser();
            user.setUsername(studentDto.getUsername());
            user.setEmail(studentDto.getEmail());
            user.setFirstName(studentDto.getFirstName());
            user.setLastName(studentDto.getLastName());
            userService.save(user);

            // Обновяване на ученика
            Student updatedStudent = Student.builder()
                    .id(existingStudent.getId())
                    .user(user)
                    .school(schoolService.getSchoolById(studentDto.getSchoolId()).orElseThrow())
                    .schoolClass(studentDto.getSchoolClassId() != null ? 
                            schoolClassService.getSchoolClassById(studentDto.getSchoolClassId()).orElse(null) : null)
                    .build();

            studentService.updateStudent(id, updatedStudent);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ученикът " + studentDto.getFullName() + " беше успешно обновен!");
            return "redirect:/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при обновяване на ученика: " + e.getMessage());
            return "redirect:/students/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Student> studentOpt = studentService.getStudentById(id);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                String studentName = student.getUser().getFirstName() + " " + student.getUser().getLastName();
                
                // Изтриване на ученика (User-а ще бъде изтрит автоматично заради CASCADE)
                studentService.deleteStudent(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Ученикът " + studentName + " беше успешно изтрит!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Ученикът не беше намерен!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Грешка при изтриване на ученика: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public String viewStudent(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ученикът не беше намерен!");
            return "redirect:/students";
        }

        Student student = studentOpt.get();
        StudentDto studentDto = convertToDto(student);
        
        model.addAttribute("student", studentDto);
        return "students/view";
    }

    // REST API за получаване на класове по училище (за JavaScript)
    @GetMapping("/api/school-classes")
    @ResponseBody
    public ResponseEntity<List<SchoolClass>> getSchoolClasses(@RequestParam Long schoolId) {
        List<SchoolClass> classes = schoolClassService.getSchoolClassesBySchool(schoolId);
        return ResponseEntity.ok(classes);
    }

    // Помощни методи
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

    private List<SchoolClass> getSchoolClassesForSchool(Long schoolId) {
        if (schoolId == null) {
            return new ArrayList<>();
        }
        return schoolClassService.getSchoolClassesBySchool(schoolId);
    }

} 