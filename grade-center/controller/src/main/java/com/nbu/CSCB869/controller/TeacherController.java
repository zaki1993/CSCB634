package com.nbu.CSCB869.controller;

import com.nbu.CSCB869.model.Teacher;
import com.nbu.CSCB869.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {
    private final TeacherService teacherService;

    /**
     * Displays a list of all teachers in the system.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping
    public String getAllTeachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teachers/list";
    }
}
