package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * HomeController handles the requests related to the home page and provides
 * a dashboard view for the users to navigate to various sections of the system.
 * It serves data related to students, diploma assignments, reviews, and diploma defenses.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ParentService parentService;
    private final DirectorService directorService;
    private final SchoolService schoolService;

    /**
     * Handles the GET request to the home page, prepares necessary data and
     * returns the home template to be displayed.
     *
     * @param model The model object used to pass data to the template.
     * @return The name of the home template (home.html).
     */
    @GetMapping("/")
    public String homePage(Model model) {
        // You can add more data as needed
        model.addAttribute("studentsCount", studentService.getAllStudents().size());
        model.addAttribute("teachersCount", teacherService.getAllTeachers().size());
        model.addAttribute("schoolsCount", schoolService.getAllSchools().size());
        model.addAttribute("parentsCount", parentService.getAllParents().size());
        model.addAttribute("directorsCount", directorService.getAllDirectors().size());

        return "home";
    }
}