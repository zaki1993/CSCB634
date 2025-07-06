package com.nbu.CSCB634.controller.admin;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.service.AcademicTermService;
import com.nbu.CSCB634.service.SchoolClassService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.SubjectService;
import com.nbu.CSCB634.service.auth.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final SchoolService schoolService;
    private final SchoolClassService classService;
    private final SubjectService subjectService;
    private final AcademicTermService termService;

    public AdminController(UserService userService,
                           SchoolService schoolService,
                           SchoolClassService classService,
                           SubjectService subjectService,
                           AcademicTermService termService) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.classService = classService;
        this.subjectService = subjectService;
        this.termService = termService;
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/updateRole")
    public String updateRole(@RequestParam Long userId, @RequestParam Role role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/users";
    }

    // ==================== SCHOOL MANAGEMENT ====================

    @GetMapping("/schools")
    public String listSchools(Model model) {
        model.addAttribute("schools", schoolService.getAll());
        return "admin/schools";
    }

    @PostMapping("/schools/create")
    public String createSchool(@ModelAttribute School school) {
        schoolService.createSchool(school);
        return "redirect:/admin/schools";
    }

    @PostMapping("/schools/delete")
    public String deleteSchool(@RequestParam Long id) {
        schoolService.deleteSchool(id);
        return "redirect:/admin/schools";
    }

    // ==================== CLASS MANAGEMENT ====================

    @GetMapping("/classes")
    public String listClasses(Model model) {
        model.addAttribute("classes", classService.getAll());
        model.addAttribute("schools", schoolService.getAll());
        return "admin/classes";
    }

    @PostMapping("/classes/create")
    public String createClass(@ModelAttribute SchoolClass schoolClass) {
        classService.createSchoolClass(schoolClass);
        return "redirect:/admin/classes";
    }

    @PostMapping("/classes/delete")
    public String deleteClass(@RequestParam Long id) {
        classService.deleteSchoolClass(id);
        return "redirect:/admin/classes";
    }

    // ==================== SUBJECT MANAGEMENT ====================

    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAll());
        return "admin/subjects";
    }

    @PostMapping("/subjects/create")
    public String createSubject(@ModelAttribute Subject subject) {
        subjectService.createSubject(subject);
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/delete")
    public String deleteSubject(@RequestParam Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/admin/subjects";
    }

    // ==================== ACADEMIC TERM MANAGEMENT ====================

    @GetMapping("/terms")
    public String listTerms(Model model) {
        model.addAttribute("terms", termService.getAll());
        model.addAttribute("schools", schoolService.getAll());
        model.addAttribute("subjects", subjectService.getAll());
        return "admin/terms";
    }

    @PostMapping("/terms/create")
    public String createTerm(@ModelAttribute AcademicTerm term) {
        termService.createAcademicTerm(term);
        return "redirect:/admin/terms";
    }

    @PostMapping("/terms/delete")
    public String deleteTerm(@RequestParam Long id) {
        termService.deleteAcademicTerm(id);
        return "redirect:/admin/terms";
    }
}
