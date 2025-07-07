package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.service.StudentClassEnrollmentService;
import com.nbu.CSCB634.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student-enrollment")
@RequiredArgsConstructor
public class StudentClassEnrollmentController {

    private final StudentClassEnrollmentService enrollmentService;
    private final SchoolService schoolService;

    /**
     * Показва страница за управление на записванията
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String enrollmentManagement(@RequestParam(required = false) Long schoolId, Model model) {
        // Ако няма избрано училище, покажи първото
        if (schoolId == null) {
            var schools = schoolService.getAllSchools();
            if (!schools.isEmpty()) {
                schoolId = schools.get(0).getId();
            }
        }
        
        if (schoolId != null) {
            var school = schoolService.getSchoolById(schoolId).orElse(null);
            var classes = enrollmentService.getClassesInSchool(schoolId);
            var studentsWithoutClass = enrollmentService.getStudentsWithoutClass(schoolId);
            
            model.addAttribute("selectedSchool", school);
            model.addAttribute("classes", classes);
            model.addAttribute("studentsWithoutClass", studentsWithoutClass);
            
            // Добавяме информация за всеки клас
            classes.forEach(schoolClass -> {
                var studentsInClass = enrollmentService.getStudentsInClass(schoolClass.getId());
                schoolClass.setStudents(studentsInClass); // Ако SchoolClass има такова поле
            });
        }
        
        model.addAttribute("schools", schoolService.getAllSchools());
        model.addAttribute("selectedSchoolId", schoolId);
        
        return "student-enrollment/management";
    }

    /**
     * Показва подробности за конкретен клас
     */
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public String classDetails(@PathVariable Long classId, Model model) {
        var studentsInClass = enrollmentService.getStudentsInClass(classId);
        
        // Намираме класа от всички училища
        var allSchools = schoolService.getAllSchools();
        SchoolClass schoolClass = null;
        for (var school : allSchools) {
            var classes = enrollmentService.getClassesInSchool(school.getId());
            var foundClass = classes.stream()
                    .filter(c -> c.getId().equals(classId))
                    .findFirst();
            if (foundClass.isPresent()) {
                schoolClass = foundClass.get();
                break;
            }
        }
        
        if (schoolClass == null) {
            throw new IllegalArgumentException("Класът не е намерен");
        }
        
        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("students", studentsInClass);
        model.addAttribute("studentCount", studentsInClass.size());
        
        return "student-enrollment/class-details";
    }

    /**
     * Записва ученик в клас
     */
    @PostMapping("/enroll")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String enrollStudent(@RequestParam Long studentId, 
                               @RequestParam Long classId,
                               @RequestParam(required = false) Long schoolId,
                               RedirectAttributes redirectAttributes) {
        try {
            Student enrolledStudent = enrollmentService.enrollStudentInClass(studentId, classId);
            redirectAttributes.addFlashAttribute("success", 
                    "Ученикът " + enrolledStudent.getUser().getFirstName() + " " + 
                    enrolledStudent.getUser().getLastName() + " е записан успешно в класа!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                    "Грешка при записване: " + e.getMessage());
        }
        
        return "redirect:/student-enrollment" + (schoolId != null ? "?schoolId=" + schoolId : "");
    }

    /**
     * Отписва ученик от клас
     */
    @PostMapping("/withdraw/{studentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String withdrawStudent(@PathVariable Long studentId,
                                 @RequestParam(required = false) Long schoolId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Student withdrawnStudent = enrollmentService.withdrawStudentFromClass(studentId);
            redirectAttributes.addFlashAttribute("success", 
                    "Ученикът " + withdrawnStudent.getUser().getFirstName() + " " + 
                    withdrawnStudent.getUser().getLastName() + " е отписан успешно от класа!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                    "Грешка при отписване: " + e.getMessage());
        }
        
        return "redirect:/student-enrollment" + (schoolId != null ? "?schoolId=" + schoolId : "");
    }

    /**
     * Прехвърля ученик в друг клас
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public String transferStudent(@RequestParam Long studentId,
                                 @RequestParam Long newClassId,
                                 @RequestParam(required = false) Long schoolId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Student transferredStudent = enrollmentService.transferStudentToClass(studentId, newClassId);
            redirectAttributes.addFlashAttribute("success", 
                    "Ученикът " + transferredStudent.getUser().getFirstName() + " " + 
                    transferredStudent.getUser().getLastName() + " е прехвърлен успешно в новия клас!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                    "Грешка при прехвърляне: " + e.getMessage());
        }
        
        return "redirect:/student-enrollment" + (schoolId != null ? "?schoolId=" + schoolId : "");
    }

    /**
     * REST API endpoint за получаване на ученици без клас
     */
    @GetMapping("/api/students-without-class/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    @ResponseBody
    public List<Student> getStudentsWithoutClass(@PathVariable Long schoolId) {
        return enrollmentService.getStudentsWithoutClass(schoolId);
    }

    /**
     * REST API endpoint за получаване на ученици в клас
     */
    @GetMapping("/api/students-in-class/{classId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    @ResponseBody
    public List<Student> getStudentsInClass(@PathVariable Long classId) {
        return enrollmentService.getStudentsInClass(classId);
    }

    /**
     * REST API endpoint за получаване на класове в училище
     */
    @GetMapping("/api/classes-in-school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    @ResponseBody
    public List<SchoolClass> getClassesInSchool(@PathVariable Long schoolId) {
        return enrollmentService.getClassesInSchool(schoolId);
    }

    /**
     * REST API endpoint за проверка дали ученик може да бъде записан в клас
     */
    @GetMapping("/api/can-enroll/{studentId}/{classId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    @ResponseBody
    public boolean canEnrollStudentInClass(@PathVariable Long studentId, @PathVariable Long classId) {
        return enrollmentService.canEnrollStudentInClass(studentId, classId);
    }
} 