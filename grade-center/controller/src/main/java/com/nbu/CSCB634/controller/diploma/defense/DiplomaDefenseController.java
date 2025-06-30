package com.nbu.CSCB634.controller.diploma.defense;

import com.nbu.CSCB634.controller.diploma.defense.dto.DiplomaDefenseDTO;
import com.nbu.CSCB634.global.AccessControlConfig;
import com.nbu.CSCB634.model.diploma.defense.DiplomaDefense;
import com.nbu.CSCB634.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB634.model.diploma.thesis.review.ReviewOutcome;
import com.nbu.CSCB634.service.StudentService;
import com.nbu.CSCB634.service.TeacherService;
import com.nbu.CSCB634.service.diploma.defense.DiplomaDefenseService;
import com.nbu.CSCB634.service.diploma.thesis.DiplomaThesisService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/diploma-defenses")
public class DiplomaDefenseController {

    private final DiplomaDefenseService diplomaDefenseService;
    private final DiplomaThesisService thesisService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public DiplomaDefenseController(DiplomaDefenseService diplomaDefenseService, DiplomaThesisService thesisService, StudentService studentService, TeacherService teacherService) {
        this.diplomaDefenseService = diplomaDefenseService;
        this.thesisService = thesisService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    @GetMapping
    public String getAllDiplomaDefenses(Model model) {
        List<DiplomaDefense> defenses = diplomaDefenseService.getAllDefenses();

        List<DiplomaDefenseDTO> defenseDTOs = defenses.stream().map(defense -> {
            DiplomaDefenseDTO dto = new DiplomaDefenseDTO();
            dto.setId(defense.getId());
            dto.setDefenseDate(defense.getDefenseDate());
            dto.setLocation(defense.getLocation());

            dto.setStudentNames(defense.getStudents().stream()
                    .map(studentService::getStudentById)
                    .map(s -> s.getName() + "-" + s.getFn())
                    .collect(Collectors.toSet()));
            dto.setTeacherNames(defense.getTeachers().stream()
                    .map(teacherService::getTeacherById)
                    .map(Teacher::getName)
                    .collect(Collectors.toSet()));

            dto.setDelegate(defense);
            return dto;
        }).toList();

        model.addAttribute("defenses", defenseDTOs);
        return "diploma-defenses/list";
    }

    @GetMapping("/schedule")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String createDiplomaDefenseForm(Model model) {
        // Fetch only theses with positive reviews
        List<DiplomaThesis> positiveReviewedTheses = thesisService.getAllTheses()
                .stream()
                .filter(thesis -> thesis.getReview() != null &&
                        ReviewOutcome.POSITIVE == thesis.getReview().getReviewOutcome() && !diplomaDefenseService.isThesisDefended(thesis.getId())
                )
                .collect(Collectors.toList());

        model.addAttribute("diplomaDefense", new DiplomaDefense());
        model.addAttribute("theses", positiveReviewedTheses);
        return "diploma-defenses/create-edit";
    }


    @PostMapping("/schedule-defense")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String saveDiplomaDefense(@ModelAttribute("diplomaDefense") DiplomaDefense def,
                                     @RequestParam(value = "thesesIds", required = false) List<Long> thesesIds,
                                     @RequestParam("defenseDate") String defenseDate,
                                     @RequestParam("location") String location) {
        if (thesesIds == null) {
            thesesIds = List.of();
        }
        List<Long> students = new ArrayList<>();
        List<Long> teachers = new ArrayList<>();
        // validate if they exist
        for (Long thesisId : thesesIds) {
            DiplomaThesis t = thesisService.getThesisById(thesisId);
            students.add(t.getAssignment().getStudent().getId());
            teachers.add(t.getAssignment().getTeacher().getId());
        }
        def.setTheses(thesesIds);
        def.setStudents(students);
        def.setTeachers(teachers);

        LocalDateTime date = LocalDateTime.parse(defenseDate);
        def.setDefenseDate(date);
        def.setLocation(location);
        diplomaDefenseService.scheduleDefense(def);
        return "redirect:/diploma-defenses";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String editDiplomaDefense(@PathVariable("id") Long id, Model model) {
        DiplomaDefense diplomaDefense = diplomaDefenseService.getDefenseById(id);
        List<DiplomaThesis> positiveReviewedTheses = thesisService.getAllTheses()
                .stream()
                .filter(thesis -> thesis.getReview() != null &&
                        ReviewOutcome.POSITIVE == thesis.getReview().getReviewOutcome() && !diplomaDefenseService.isThesisDefended(thesis.getId())
                )
                .collect(Collectors.toList());
        model.addAttribute("diplomaDefense", diplomaDefense);
        model.addAttribute("theses", positiveReviewedTheses);
        return "diploma-defenses/create-edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String updateDiplomaDefense(@PathVariable("id") Long id,
                                       @ModelAttribute("diplomaDefense") DiplomaDefense diplomaDefense,
                                       @RequestParam(value = "thesesIds", required = false) List<Long> thesesIds,
                                       @RequestParam("defenseDate") String defenseDate,
                                       @RequestParam("location") String location) {
        if (thesesIds == null) {
            thesesIds = List.of();
        }
        List<Long> students = new ArrayList<>();
        List<Long> teachers = new ArrayList<>();
        // validate if they exist
        for (Long thesisId : thesesIds) {
            DiplomaThesis t = thesisService.getThesisById(thesisId);
            students.add(t.getAssignment().getStudent().getId());
            teachers.add(t.getAssignment().getTeacher().getId());
        }
        diplomaDefense.setTheses(thesesIds);
        diplomaDefense.setStudents(students);
        diplomaDefense.setTeachers(teachers);
        diplomaDefense.setDefenseDate(LocalDateTime.parse(defenseDate));
        diplomaDefense.setLocation(location);

        diplomaDefenseService.editDiplomaDefense(id, diplomaDefense);
        return "redirect:/diploma-defenses";
    }

    @GetMapping("/grades/{defenseId}")
    public String viewGrades(@PathVariable Long defenseId, Model model) {
        DiplomaDefense diplomaDefense = diplomaDefenseService.getDefenseById(defenseId);

        // Fetch related theses based on stored IDs
        List<DiplomaThesis> thesisList = thesisService.getThesesByIds(diplomaDefense.getTheses());

        // Fetch grades mapped to thesis IDs
        Map<Long, Double> grades = diplomaDefense.getGradles();

        // Map to track which theses are supervised by the logged-in teacher
        Map<Long, Boolean> isEditable = thesisList.stream()
                .collect(Collectors.toMap(
                        DiplomaThesis::getId,
                        thesis -> thesis.getAssignment().getTeacher().equals(getTeacherFromContext()))
                );


        model.addAttribute("diplomaDefense", diplomaDefense);
        model.addAttribute("thesisList", thesisList);
        model.addAttribute("grades", grades);
        model.addAttribute("isEditable", isEditable);

        return "diploma-defenses/grades";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String deleteDiplomaDefense(@PathVariable("id") Long id) {
        diplomaDefenseService.deleteDefense(id);
        return "redirect:/diploma-defenses";
    }

    @PostMapping("/grades/update/{defenseId}/{thesisId}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String updateGrade(@PathVariable Long defenseId,
                              @PathVariable Long thesisId,
                              @RequestParam Double grade) {

        // Get the DiplomaDefense object by ID
        DiplomaDefense diplomaDefense = diplomaDefenseService.getDefenseById(defenseId);

        // Check if the thesis is part of the current defense
        if (!diplomaDefense.getTheses().contains(thesisId)) {
            return "redirect:/diploma-defenses";
        }
        // Get the current logged-in teacher
        Teacher currentTeacher = getTeacherFromContext();
        DiplomaThesis thesis = thesisService.getThesisById(thesisId);

        // Check if the teacher is assigned to this thesis
        if (!thesis.getAssignment().getTeacher().equals(currentTeacher)) {
            return "redirect:/diploma-defenses";
        }

        // Update the grade for the specific thesis
        diplomaDefense.getGradles().put(thesisId, grade);

        // Save the updated DiplomaDefense
        diplomaDefenseService.saveDefense(diplomaDefense);

        return "redirect:/diploma-defenses/grades/" + defenseId;
    }

    private Teacher getTeacherFromContext() {
        String username = AccessControlConfig.getUsername();
        return teacherService.getTeacherByUsername(username);
    }
}
