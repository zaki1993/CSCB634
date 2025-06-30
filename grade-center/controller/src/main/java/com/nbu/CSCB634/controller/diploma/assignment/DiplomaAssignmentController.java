package com.nbu.CSCB634.controller.diploma.assignment;

import com.nbu.CSCB634.global.AccessControlConfig;
import com.nbu.CSCB634.model.diploma.assignment.DiplomaAssignment;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.service.diploma.assignment.DiplomaAssignmentService;
import com.nbu.CSCB634.service.StudentService;
import com.nbu.CSCB634.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/diploma-assignments")
@RequiredArgsConstructor
public class DiplomaAssignmentController {

    private final DiplomaAssignmentService diplomaAssignmentService;

    private final StudentService studentService;
    private final TeacherService teacherService;

    /**
     * Displays all diploma assignments in the system.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping
    public String getAllDiplomaAssignments(Model model) {
        List<DiplomaAssignment> assignments = diplomaAssignmentService.getAllAssignments();
        // The student can view only approved diploma assignments
        // The teacher can view all diploma assignments
        if (AccessControlConfig.isStudent()) {
            assignments = assignments.stream()
                    .filter(DiplomaAssignment::isApproved)
                    .collect(Collectors.toList());
        }
        model.addAttribute("assignments", assignments);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "diploma-assignments/list";
    }

    /**
     * Displays all diploma assignments for specific username.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/user")
    public String getDiplomaAssignmentsForUser(Model model) {
        List<DiplomaAssignment> assignments = diplomaAssignmentService.getAllAssignments();
        assignments = assignments.stream()
                .filter(a -> getAssignmentUsername(a).equals(AccessControlConfig.getUsername()))
                .collect(Collectors.toList());
        model.addAttribute("assignments", assignments);
        model.addAttribute("userOnly", true);
        return "diploma-assignments/list";
    }

    /**
     * Get assignment username depending if we are teacher or student.
     *
     * @param a
     * @return
     */
    private String getAssignmentUsername(DiplomaAssignment a) {
        if (AccessControlConfig.isStudent()) {
            return a.getStudent().getUsername();
        }
        return a.getTeacher().getUsername();
    }

    /**
     * Displays the form to create a new diploma assignment.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/create")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String createDiplomaAssignmentForm(Model model) {
        model.addAttribute("diplomaAssignment", new DiplomaAssignment());
        model.addAttribute("students", studentService.getAllStudents());
        return "diploma-assignments/create-edit";
    }

    /**
     * Saves a new diploma assignment to the system.
     *
     * @param diplomaAssignment The diploma assignment object to be saved.
     * @return Redirects to the list of all assignments after saving.
     */
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/create")
    public String saveDiplomaAssignment(@ModelAttribute("diplomaAssignment") DiplomaAssignment diplomaAssignment,
                                        @RequestParam("student.fn") String studentFn) {
        // Set the student
        Student s = studentService.getStudentByFn(studentFn);
        diplomaAssignment.setStudent(s);

        // Set the teacher to be current logged-in user
        Teacher t = getTeacherFromContext();
        diplomaAssignment.setTeacher(t);

        diplomaAssignmentService.saveAssignment(diplomaAssignment);

        // redirect to edit page
        return "redirect:/diploma-assignments";
    }

    /**
     * Approves the diploma assignment.
     *
     * @param id The diploma assignment id.
     * @return Redirects to the list of all assignments after saving.
     */
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/approve/{id}")
    public String approveDiplomaAssignment(@PathVariable("id") Long id, Model model) {
        diplomaAssignmentService.approveDiplomaAssignment(id);

        return "redirect:/diploma-assignments";
    }

    /**
     * Displays the form to edit an existing diploma assignment.
     *
     * @param id    The ID of the diploma assignment to edit.
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String getEditDiplomaAssignment(@PathVariable("id") Long id, Model model) {
        DiplomaAssignment diplomaAssignment = diplomaAssignmentService.getAssignmentById(id);
        model.addAttribute("diplomaAssignment", diplomaAssignment);
        model.addAttribute("students", studentService.getAllStudents());
        return "diploma-assignments/create-edit";
    }

    /**
     * Updates an existing diploma assignment.
     *
     * @param id                The ID of the diploma assignment to update.
     * @param diplomaAssignment The updated diploma assignment object.
     * @return Redirects to the list of all assignments after updating.
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String editDiplomaAssignment(@PathVariable("id") Long id,
                                          @ModelAttribute("diplomaAssignment") DiplomaAssignment diplomaAssignment,
                                          @RequestParam("student.fn") String studentFn) {
        DiplomaAssignment toModify = diplomaAssignmentService.getAssignmentById(id);

        Student s = studentService.getStudentByFn(studentFn);
        toModify.setStudent(s);
        toModify.setTasks(diplomaAssignment.getTasks());
        toModify.setPurpose(diplomaAssignment.getPurpose());
        toModify.setTechnologies(diplomaAssignment.getTechnologies());
        toModify.setTitle(diplomaAssignment.getTitle());

        diplomaAssignmentService.saveAssignment(toModify);
        return "redirect:/diploma-assignments";
    }

    /**
     * Deletes a diploma assignment by its ID.
     *
     * @param id The ID of the diploma assignment to delete.
     * @return Redirects to the list of all assignments after deletion.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity deleteDiplomaAssignment(@PathVariable("id") Long id) {
        diplomaAssignmentService.deleteAssignment(id);
        return ResponseEntity.ok().build();
    }

    private Teacher getTeacherFromContext() {
        String username = AccessControlConfig.getUsername();
        return teacherService.getTeacherByUsername(username);
    }
}
