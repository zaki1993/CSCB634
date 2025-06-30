package com.nbu.CSCB869.controller;

import com.nbu.CSCB869.service.*;
import com.nbu.CSCB869.service.diploma.assignment.DiplomaAssignmentService;
import com.nbu.CSCB869.service.diploma.defense.DiplomaDefenseService;
import com.nbu.CSCB869.service.diploma.thesis.review.ThesisReviewService;
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
    private final DiplomaAssignmentService diplomaAssignmentService;
    private final ThesisReviewService reviewService;
    private final DiplomaDefenseService diplomaDefenseService;

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
        model.addAttribute("diplomaAssignmentsCount", diplomaAssignmentService.getAllAssignments().size());
        model.addAttribute("reviewsCount", reviewService.getAllReviews().size());
        model.addAttribute("diplomaDefensesCount", diplomaDefenseService.getAllDefenses().size());

        return "home";
    }
}
