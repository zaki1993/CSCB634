package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.controller.diploma.thesis.dto.DiplomaThesisReportDTO;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB634.service.StudentService;
import com.nbu.CSCB634.service.TeacherService;
import com.nbu.CSCB634.service.diploma.defense.DiplomaDefenseService;
import com.nbu.CSCB634.service.diploma.thesis.DiplomaThesisService;
import com.nbu.CSCB634.service.diploma.thesis.review.ThesisReviewService;
import org.springframework.stereotype.Controller; // Change from @RestController to @Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller  // Change this to @Controller
@RequestMapping("/reports")
public class ReportController {

    private final StudentService studentService;
    private final DiplomaThesisService diplomaThesisService;
    private final ThesisReviewService thesisReviewService;
    private final DiplomaDefenseService diplomaDefenseService;
    private final TeacherService teacherService;

    public ReportController(StudentService studentService,
                            DiplomaThesisService diplomaThesisService,
                            ThesisReviewService thesisReviewService,
                            DiplomaDefenseService diplomaDefenseService,
                            TeacherService teacherService) {
        this.studentService = studentService;
        this.diplomaThesisService = diplomaThesisService;
        this.thesisReviewService = thesisReviewService;
        this.diplomaDefenseService = diplomaDefenseService;
        this.teacherService = teacherService;
    }

    @GetMapping
    public String showReports(Model model) {
        // Return the name of the view (reports.html)
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "reports";
    }

    @GetMapping("/graduated-students")
    public String getGraduatedStudents(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate, Model model) {
        List<Student> graduatedStudents = studentService.getGraduatedStudentsInPeriod(startDate, endDate);
        model.addAttribute("graduatedStudents", graduatedStudents);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "reports";  // Return the reports page with the data
    }

    @GetMapping("/theses-grades")
    public String getThesesWithGrades(@RequestParam double minGrade, @RequestParam double maxGrade, Model model) {
        List<DiplomaThesis> thesesGrades = diplomaThesisService.getThesesWithGradesInRange(minGrade, maxGrade);
        List<DiplomaThesisReportDTO> theses = new ArrayList<>();
        for (DiplomaThesis dt : thesesGrades) {
            theses.add(new DiplomaThesisReportDTO(dt.getTitle(), dt.getAssignment().getStudent().getName(), dt.getAssignment().getTeacher().getName(), diplomaDefenseService.getThesisGradle(dt.getId())));
        }
        model.addAttribute("thesesGrades", theses);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "reports";  // Return the reports page with the data
    }

    @GetMapping("/negative-reviews")
    public String getNegativeReviewsCount(Model model) {
        long negativeReviewsCount = thesisReviewService.getNegativeReviewsCount();
        model.addAttribute("negativeReviewsCount", negativeReviewsCount);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "reports";  // Return the reports page with the data
    }

    @GetMapping("/average-students")
    public String getAverageStudentsPerDefense(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate, Model model) {
        Double averageStudentsPerDefense = diplomaDefenseService.getAverageStudentsPerDefenseInPeriod(startDate, endDate);
        model.addAttribute("averageStudentsPerDefense", averageStudentsPerDefense);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "reports";  // Return the reports page with the data
    }

    @GetMapping("/successful-defenses")
    public String getSuccessfulDefensesByTeacher(@RequestParam Long teacherId, Model model) {
        long successfulDefensesCount = teacherService.getSuccessfulDefensesByTeacher(teacherId);
        model.addAttribute("successfulDefensesCount", successfulDefensesCount);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "reports";  // Return the reports page with the data
    }
}
