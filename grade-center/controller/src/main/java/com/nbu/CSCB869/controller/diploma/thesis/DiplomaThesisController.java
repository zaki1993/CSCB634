package com.nbu.CSCB869.controller.diploma.thesis;

import com.nbu.CSCB869.global.AccessControlConfig;
import com.nbu.CSCB869.model.Student;
import com.nbu.CSCB869.model.diploma.assignment.DiplomaAssignment;
import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB869.service.StudentService;
import com.nbu.CSCB869.service.TeacherService;
import com.nbu.CSCB869.service.diploma.assignment.DiplomaAssignmentService;
import com.nbu.CSCB869.service.diploma.thesis.DiplomaThesisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diploma-thesis")
@RequiredArgsConstructor
public class DiplomaThesisController {

    private final DiplomaAssignmentService diplomaAssignmentService;
    private final DiplomaThesisService diplomaThesisService;

    private final StudentService studentService;
    private final TeacherService teacherService;

    /**
     * Displays all diploma assignments for specific username.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/{diplomaAssignmentId}/create")
    @PreAuthorize("hasAuthority('STUDENT')")
    public String getDiplomaThesisPage(@PathVariable("diplomaAssignmentId") Long diplomaAssignmentId, Model model) {
        // Validate if the diploma assignment is valid
        DiplomaAssignment diplomaAssignment = diplomaAssignmentService.getAssignmentById(diplomaAssignmentId);

        // Create the thesis
        DiplomaThesis diplomaThesis = new DiplomaThesis();

        model.addAttribute("diplomaThesis", diplomaThesis);
        model.addAttribute("diplomaAssignment", diplomaAssignment);
        return "diploma-thesis/create-edit";
    }

    /**
     * Displays all diploma assignments for specific username.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @PostMapping("/{diplomaAssignmentId}/create")
    @PreAuthorize("hasAuthority('STUDENT')")
    public String createDiplomaThesis(@ModelAttribute("diplomaThesis") DiplomaThesis diplomaThesis, @PathVariable("diplomaAssignmentId") Long diplomaAssignmentId, Model model) {
        // Validate if the diploma assignment exist
        DiplomaAssignment diplomaAssignment = diplomaAssignmentService.getAssignmentById(diplomaAssignmentId);

        // Only can create thesis for approved diploma assignment
        // And only the student which is assigned can create it
        if (diplomaAssignment.isApproved() &&
                AccessControlConfig.getUsername().equals(diplomaAssignment.getStudent().getUsername())) {
            diplomaThesis.setAssignment(diplomaAssignment);

            diplomaThesisService.createDiplomaThesis(diplomaThesis);

            model.addAttribute("diplomaThesis", diplomaThesis);
            model.addAttribute("diplomaAssignment", diplomaAssignment);
            return "diploma-thesis/create-edit";
        }

        return "redirect:/diploma-assignments/user";
    }

    /**
     * Displays the form to edit an existing diploma thesis.
     *
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public String getEditDiplomaThesis(@PathVariable("id") Long id, Model model) {
        DiplomaThesis thesis = diplomaThesisService.getThesisById(id);
        if (AccessControlConfig.getUsername().equals(thesis.getAssignment().getStudent().getUsername())) {
            model.addAttribute("diplomaThesis", thesis);
            model.addAttribute("diplomaAssignment", thesis.getAssignment());
            return "diploma-thesis/create-edit";
        }
        return "redirect:/diploma-assignments/user";
    }

    /**
     * Updates an existing diploma assignment.
     *
     * @param id The ID of the diploma thesis to update.
     * @param thesis The updated diploma thesis object.
     * @return Redirects to the diploma thesis page.
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public String editDiplomaThesis(@PathVariable("id") Long id,
                                    @ModelAttribute("diplomaThesis") DiplomaThesis thesis, Model model) {

        DiplomaThesis toUpdate = diplomaThesisService.getThesisById(id);
        if (AccessControlConfig.getUsername().equals(toUpdate.getAssignment().getStudent().getUsername())) {
            toUpdate.setTitle(thesis.getTitle());
            toUpdate.setText(thesis.getText());

            diplomaThesisService.saveThesis(toUpdate);
            model.addAttribute("diplomaThesis", thesis);
            model.addAttribute("diplomaAssignment", thesis.getAssignment());
            return "redirect:/diploma-thesis/edit/" + id;
        }
        return "redirect:/diploma-assignments/user";
    }
}