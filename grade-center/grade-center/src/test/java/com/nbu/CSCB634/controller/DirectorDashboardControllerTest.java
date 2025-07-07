package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.*;
import com.nbu.CSCB634.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DirectorDashboardControllerTest {

    @Mock
    private DirectorService directorService;
    @Mock
    private UserService userService;
    @Mock
    private StudentService studentService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private ParentService parentService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private GradeService gradeService;
    @Mock
    private AbsenceService absenceService;
    @Mock
    private AcademicTermService academicTermService;
    @Mock
    private Model model;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private DirectorDashboardController directorDashboardController;

    private MockMvc mockMvc;

    // Test data
    private User user;
    private School school;
    private Student student;
    private Teacher teacher;
    private Parent parent;
    private Director director;
    private Subject subject;
    private Grade grade;
    private Absence absence;
    private AcademicTerm academicTerm;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(directorDashboardController).build();
        
        // Initialize test data
        user = User.builder()
                .id(1L)
                .username("director")
                .firstName("Director")
                .lastName("User")
                .email("director@test.com")
                .role(Role.DIRECTOR)
                .build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("Test Address")
                .build();

        student = Student.builder()
                .id(1L)
                .school(school)
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .user(user)
                .build();

        parent = Parent.builder()
                .id(1L)
                .user(user)
                .students(new HashSet<>(Arrays.asList(student)))
                .build();

        director = Director.builder()
                .id(1L)
                .user(user)
                .school(school)
                .build();

        subject = Subject.builder()
                .id(1L)
                .name("Mathematics")
                .build();

        grade = Grade.builder()
                .id(1L)
                .value(5)
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .dateAwarded(LocalDate.now())
                .build();

        absence = Absence.builder()
                .id(1L)
                .student(student)
                .absenceDate(LocalDate.now())
                .justified(true)
                .build();

        academicTerm = AcademicTerm.builder()
                .id(1L)
                .name("Term 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .build();
    }

    @Test
    void testDashboard_Success() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(Arrays.asList(teacher));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAcademicTermsBySchoolId(1L)).thenReturn(Arrays.asList(academicTerm));
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));
        when(gradeService.getGradesByStudentId(1L)).thenReturn(Arrays.asList(grade));
        when(absenceService.getAbsencesByStudentId(1L)).thenReturn(Arrays.asList(absence));

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("director/dashboard", result);
        verify(model).addAttribute("school", school);
        verify(model).addAttribute("director", director);
        verify(model).addAttribute("studentsCount", 1);
        verify(model).addAttribute("teachersCount", 1);
        verify(model).addAttribute("parentsCount", 1);
        verify(model).addAttribute("subjectsCount", 1);
        verify(model).addAttribute("academicTermsCount", 1);
        verify(model).addAttribute("totalGrades", 1);
        verify(model).addAttribute("totalAbsences", 1);
        verify(model).addAttribute(eq("averageGrade"), any(Double.class));
        verify(model).addAttribute(eq("gradeDistribution"), any(Map.class));
        verify(model).addAttribute(eq("gradesBySubject"), any(Map.class));
        verify(model).addAttribute(eq("gradesByTeacher"), any(Map.class));
        verify(model).addAttribute(eq("recentGrades"), any(List.class));
        verify(model).addAttribute(eq("recentAbsences"), any(List.class));
    }

    @Test
    void testDashboard_UserNotFound() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.empty());

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("User not found"));
    }

    @Test
    void testDashboard_DirectorNotFound() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.empty());

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Director not found"));
    }

    @Test
    void testDashboard_WithNullData() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(null);
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(null);
        when(subjectService.getAllSubjects()).thenReturn(null);
        when(academicTermService.getAcademicTermsBySchoolId(1L)).thenReturn(null);
        when(parentService.getAllParents()).thenReturn(null);
        when(gradeService.getGradesByStudentId(anyLong())).thenReturn(null);
        when(absenceService.getAbsencesByStudentId(anyLong())).thenReturn(null);

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("director/dashboard", result);
        verify(model).addAttribute("studentsCount", 0);
        verify(model).addAttribute("teachersCount", 0);
        verify(model).addAttribute("parentsCount", 0);
        verify(model).addAttribute("subjectsCount", 0);
        verify(model).addAttribute("academicTermsCount", 0);
        verify(model).addAttribute("totalGrades", 0);
        verify(model).addAttribute("totalAbsences", 0);
    }

    @Test
    void testDashboard_WithException() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenThrow(new RuntimeException("Database error"));

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Error loading dashboard"));
    }

    @Test
    void testViewStudents_Success() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));

        // When
        String result = directorDashboardController.viewStudents(model, authentication);

        // Then
        assertEquals("director/students", result);
        verify(model).addAttribute("students", Arrays.asList(student));
        verify(model).addAttribute("school", school);
    }

    @Test
    void testViewStudents_UserNotFound() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            directorDashboardController.viewStudents(model, authentication);
        });
    }

    @Test
    void testViewStudents_DirectorNotFound() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            directorDashboardController.viewStudents(model, authentication);
        });
    }

    @Test
    void testViewTeachers_Success() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(Arrays.asList(teacher));

        // When
        String result = directorDashboardController.viewTeachers(model, authentication);

        // Then
        assertEquals("director/teachers", result);
        verify(model).addAttribute("teachers", Arrays.asList(teacher));
        verify(model).addAttribute("school", school);
    }

    @Test
    void testViewTeachers_UserNotFound() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            directorDashboardController.viewTeachers(model, authentication);
        });
    }

    @Test
    void testViewParents_Success() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));

        // When
        String result = directorDashboardController.viewParents(model, authentication);

        // Then
        assertEquals("director/parents", result);
        verify(model).addAttribute(eq("parents"), any(List.class));
        verify(model).addAttribute("school", school);
    }

    @Test
    void testViewStatistics_Success() {
        // Given
        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(gradeService.getGradesByStudentId(1L)).thenReturn(Arrays.asList(grade));
        when(absenceService.getAbsencesByStudentId(1L)).thenReturn(Arrays.asList(absence));

        // When
        String result = directorDashboardController.viewStatistics(model, authentication);

        // Then
        assertEquals("director/statistics", result);
        verify(model).addAttribute("school", school);
        verify(model).addAttribute("studentsCount", 1);
        verify(model).addAttribute("totalGrades", 1);
        verify(model).addAttribute("totalAbsences", 1);
        verify(model).addAttribute(eq("averageGrade"), any(Double.class));
        verify(model).addAttribute(eq("gradeDistribution"), any(Map.class));
        verify(model).addAttribute(eq("subjectStats"), any(Map.class));
        verify(model).addAttribute(eq("teacherStats"), any(Map.class));
        verify(model).addAttribute(eq("recentGrades"), any(List.class));
        verify(model).addAttribute(eq("recentAbsences"), any(List.class));
    }

    @Test
    void testGetParentsBySchoolId_Success() {
        // Given
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));

        // When
        List<Parent> result = directorDashboardController.getParentsBySchoolId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(parent, result.get(0));
    }

    @Test
    void testGetParentsBySchoolId_WithNullStudents() {
        // Given
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(null);
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));

        // When
        List<Parent> result = directorDashboardController.getParentsBySchoolId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetParentsBySchoolId_WithEmptyStudents() {
        // Given
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(new ArrayList<>());
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));

        // When
        List<Parent> result = directorDashboardController.getParentsBySchoolId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateAverageGrade_WithValidGrades() {
        // Given
        Grade grade1 = Grade.builder().value(4).build();
        Grade grade2 = Grade.builder().value(5).build();
        Grade grade3 = Grade.builder().value(3).build();
        List<Grade> grades = Arrays.asList(grade1, grade2, grade3);

        // When
        double result = directorDashboardController.calculateAverageGrade(grades);

        // Then
        assertEquals(4.0, result, 0.01);
    }

    @Test
    void testCalculateAverageGrade_WithEmptyList() {
        // Given
        List<Grade> grades = new ArrayList<>();

        // When
        double result = directorDashboardController.calculateAverageGrade(grades);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateAverageGrade_WithNullList() {
        // When
        double result = directorDashboardController.calculateAverageGrade(null);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateGradeDistribution_WithValidGrades() {
        // Given
        Grade grade1 = Grade.builder().value(4).build();
        Grade grade2 = Grade.builder().value(5).build();
        Grade grade3 = Grade.builder().value(4).build();
        List<Grade> grades = Arrays.asList(grade1, grade2, grade3);

        // When
        Map<Integer, Long> result = directorDashboardController.calculateGradeDistribution(grades);

        // Then
        assertEquals(2, result.size());
        assertEquals(2L, result.get(4));
        assertEquals(1L, result.get(5));
    }

    @Test
    void testCalculateGradeDistribution_WithEmptyList() {
        // Given
        List<Grade> grades = new ArrayList<>();

        // When
        Map<Integer, Long> result = directorDashboardController.calculateGradeDistribution(grades);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateGradeDistribution_WithNullList() {
        // When
        Map<Integer, Long> result = directorDashboardController.calculateGradeDistribution(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testDashboard_WithGradesContainingNullSubjects() {
        // Given
        Grade gradeWithNullSubject = Grade.builder()
                .id(2L)
                .value(4)
                .student(student)
                .subject(null)
                .teacher(teacher)
                .dateAwarded(LocalDate.now())
                .build();

        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(Arrays.asList(teacher));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAcademicTermsBySchoolId(1L)).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getGradesByStudentId(1L)).thenReturn(Arrays.asList(grade, gradeWithNullSubject));
        when(absenceService.getAbsencesByStudentId(1L)).thenReturn(Arrays.asList(absence));

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("director/dashboard", result);
        verify(model).addAttribute("totalGrades", 2);
        verify(model).addAttribute(eq("gradesBySubject"), any(Map.class));
    }

    @Test
    void testDashboard_WithGradesContainingNullTeachers() {
        // Given
        Grade gradeWithNullTeacher = Grade.builder()
                .id(2L)
                .value(4)
                .student(student)
                .subject(subject)
                .teacher(null)
                .dateAwarded(LocalDate.now())
                .build();

        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(Arrays.asList(teacher));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAcademicTermsBySchoolId(1L)).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getGradesByStudentId(1L)).thenReturn(Arrays.asList(grade, gradeWithNullTeacher));
        when(absenceService.getAbsencesByStudentId(1L)).thenReturn(Arrays.asList(absence));

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("director/dashboard", result);
        verify(model).addAttribute("totalGrades", 2);
        verify(model).addAttribute(eq("gradesByTeacher"), any(Map.class));
    }

    @Test
    void testDashboard_WithGradesContainingNullDates() {
        // Given
        Grade gradeWithNullDate = Grade.builder()
                .id(2L)
                .value(4)
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .dateAwarded(null)
                .build();

        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(Arrays.asList(teacher));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAcademicTermsBySchoolId(1L)).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getGradesByStudentId(1L)).thenReturn(Arrays.asList(grade, gradeWithNullDate));
        when(absenceService.getAbsencesByStudentId(1L)).thenReturn(Arrays.asList(absence));

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("director/dashboard", result);
        verify(model).addAttribute("totalGrades", 2);
        verify(model).addAttribute(eq("recentGrades"), any(List.class));
    }

    @Test
    void testDashboard_WithAbsencesContainingNullDates() {
        // Given
        Absence absenceWithNullDate = Absence.builder()
                .id(2L)
                .student(student)
                .absenceDate(null)
                .justified(false)
                .build();

        when(authentication.getName()).thenReturn("director");
        when(userService.findByUsername("director")).thenReturn(Optional.of(user));
        when(directorService.getDirectorByUserId(1L)).thenReturn(Optional.of(director));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(teacherService.getTeachersBySchoolId(1L)).thenReturn(Arrays.asList(teacher));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAcademicTermsBySchoolId(1L)).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getGradesByStudentId(1L)).thenReturn(Arrays.asList(grade));
        when(absenceService.getAbsencesByStudentId(1L)).thenReturn(Arrays.asList(absence, absenceWithNullDate));

        // When
        String result = directorDashboardController.dashboard(model, authentication);

        // Then
        assertEquals("director/dashboard", result);
        verify(model).addAttribute("totalAbsences", 2);
        verify(model).addAttribute(eq("recentAbsences"), any(List.class));
    }
} 