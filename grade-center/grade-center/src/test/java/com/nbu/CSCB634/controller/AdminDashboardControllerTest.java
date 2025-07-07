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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private SchoolService schoolService;
    @Mock
    private StudentService studentService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private ParentService parentService;
    @Mock
    private DirectorService directorService;
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

    @InjectMocks
    private AdminDashboardController adminDashboardController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(adminDashboardController).build();
        
        // Initialize test data
        user = User.builder()
                .id(1L)
                .username("admin")
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.com")
                .role(Role.ADMINISTRATOR)
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
        when(userService.findAllUsers()).thenReturn(Arrays.asList(user));
        when(schoolService.getAllSchools()).thenReturn(Arrays.asList(school));
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));
        when(directorService.getAllDirectors()).thenReturn(Arrays.asList(director));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAllAcademicTerms()).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getAll()).thenReturn(Arrays.asList(grade));
        when(absenceService.getAll()).thenReturn(Arrays.asList(absence));

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 1);
        verify(model).addAttribute("totalSchools", 1);
        verify(model).addAttribute("totalStudents", 1);
        verify(model).addAttribute("totalTeachers", 1);
        verify(model).addAttribute("totalParents", 1);
        verify(model).addAttribute("totalDirectors", 1);
        verify(model).addAttribute("totalSubjects", 1);
        verify(model).addAttribute("totalAcademicTerms", 1);
        verify(model).addAttribute("totalGrades", 1);
        verify(model).addAttribute("totalAbsences", 1);
        verify(model).addAttribute(eq("averageGrade"), any(Double.class));
        verify(model).addAttribute(eq("gradeDistribution"), any(Map.class));
        verify(model).addAttribute(eq("roleDistribution"), any(Map.class));
        verify(model).addAttribute(eq("schoolStats"), any(Map.class));
        verify(model).addAttribute(eq("subjectStats"), any(Map.class));
    }

    @Test
    void testDashboard_WithNullData() {
        // Given
        when(userService.findAllUsers()).thenReturn(null);
        when(schoolService.getAllSchools()).thenReturn(null);
        when(studentService.getAllStudents()).thenReturn(null);
        when(teacherService.getAllTeachers()).thenReturn(null);
        when(parentService.getAllParents()).thenReturn(null);
        when(directorService.getAllDirectors()).thenReturn(null);
        when(subjectService.getAllSubjects()).thenReturn(null);
        when(academicTermService.getAllAcademicTerms()).thenReturn(null);
        when(gradeService.getAll()).thenReturn(null);
        when(absenceService.getAll()).thenReturn(null);

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 0);
        verify(model).addAttribute("totalSchools", 0);
        verify(model).addAttribute("totalStudents", 0);
        verify(model).addAttribute("totalTeachers", 0);
        verify(model).addAttribute("totalParents", 0);
        verify(model).addAttribute("totalDirectors", 0);
        verify(model).addAttribute("totalSubjects", 0);
        verify(model).addAttribute("totalAcademicTerms", 0);
        verify(model).addAttribute("totalGrades", 0);
        verify(model).addAttribute("totalAbsences", 0);
    }

    @Test
    void testDashboard_WithException() {
        // Given
        when(userService.findAllUsers()).thenThrow(new RuntimeException("Database error"));

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Error loading dashboard"));
    }

    @Test
    void testViewUsers_Success() {
        // Given
        when(userService.findAllUsers()).thenReturn(Arrays.asList(user));

        // When
        String result = adminDashboardController.viewUsers(model);

        // Then
        assertEquals("admin/users", result);
        verify(model).addAttribute("totalUsers", 1);
        verify(model).addAttribute(eq("usersByRole"), any(Map.class));
    }

    @Test
    void testViewUsers_WithNullData() {
        // Given
        when(userService.findAllUsers()).thenReturn(null);

        // When
        String result = adminDashboardController.viewUsers(model);

        // Then
        assertEquals("admin/users", result);
        verify(model).addAttribute("totalUsers", 0);
    }

    @Test
    void testViewUsers_WithException() {
        // Given
        when(userService.findAllUsers()).thenThrow(new RuntimeException("Database error"));

        // When
        String result = adminDashboardController.viewUsers(model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Error loading users"));
    }

    @Test
    void testViewSchools_Success() {
        // Given
        when(schoolService.getAllSchools()).thenReturn(Arrays.asList(school));
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));
        when(directorService.getAllDirectors()).thenReturn(Arrays.asList(director));
        when(gradeService.getAll()).thenReturn(Arrays.asList(grade));
        when(absenceService.getAll()).thenReturn(Arrays.asList(absence));

        // When
        String result = adminDashboardController.viewSchools(model);

        // Then
        assertEquals("admin/schools", result);
        verify(model).addAttribute("totalSchools", 1);
        verify(model).addAttribute(eq("schoolDetailedStats"), any(Map.class));
    }

    @Test
    void testViewSchools_WithException() {
        // Given
        when(schoolService.getAllSchools()).thenThrow(new RuntimeException("Database error"));

        // When
        String result = adminDashboardController.viewSchools(model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Error loading schools"));
    }

    @Test
    void testViewStatistics_Success() {
        // Given
        when(gradeService.getAll()).thenReturn(Arrays.asList(grade));
        when(absenceService.getAll()).thenReturn(Arrays.asList(absence));
        when(schoolService.getAllSchools()).thenReturn(Arrays.asList(school));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(studentService.getStudentsBySchoolId(anyLong())).thenReturn(Arrays.asList(student));
        when(gradeService.getGradesByStudentId(anyLong())).thenReturn(Arrays.asList(grade));
        when(absenceService.getAbsencesByStudentId(anyLong())).thenReturn(Arrays.asList(absence));

        // When
        String result = adminDashboardController.viewStatistics(model);

        // Then
        assertEquals("admin/statistics", result);
        verify(model).addAttribute("totalGrades", 1);
        verify(model).addAttribute("totalAbsences", 1);
        verify(model).addAttribute(eq("averageGrade"), any(Double.class));
        verify(model).addAttribute(eq("gradeDistribution"), any(Map.class));
        verify(model).addAttribute(eq("schoolStats"), any(Map.class));
        verify(model).addAttribute(eq("globalSubjectStats"), any(Map.class));
    }

    @Test
    void testViewStatistics_WithException() {
        // Given
        when(gradeService.getAll()).thenThrow(new RuntimeException("Database error"));

        // When
        String result = adminDashboardController.viewStatistics(model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Error loading statistics"));
    }

    @Test
    void testViewSchoolStatistics_Success() {
        // Given
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(studentService.getStudentsBySchoolId(1L)).thenReturn(Arrays.asList(student));
        when(gradeService.getGradesByStudentId(anyLong())).thenReturn(Arrays.asList(grade));
        when(absenceService.getAbsencesByStudentId(anyLong())).thenReturn(Arrays.asList(absence));

        // When
        String result = adminDashboardController.viewSchoolStatistics(1L, model);

        // Then
        assertEquals("admin/school-statistics", result);
        verify(model).addAttribute("school", school);
        verify(model).addAttribute("studentCount", 1);
        verify(model).addAttribute("gradeCount", 1);
        verify(model).addAttribute("absenceCount", 1);
        verify(model).addAttribute(eq("averageGrade"), any(Double.class));
        verify(model).addAttribute(eq("gradeDistribution"), any(Map.class));
        verify(model).addAttribute(eq("subjectStats"), any(Map.class));
    }

    @Test
    void testViewSchoolStatistics_SchoolNotFound() {
        // Given
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.empty());

        // When
        String result = adminDashboardController.viewSchoolStatistics(1L, model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("School not found"));
    }

    @Test
    void testViewSchoolStatistics_WithException() {
        // Given
        when(schoolService.getSchoolById(1L)).thenThrow(new RuntimeException("Database error"));

        // When
        String result = adminDashboardController.viewSchoolStatistics(1L, model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute(eq("error"), contains("Error loading school statistics"));
    }

    @Test
    void testCalculateAverageGrade_WithValidGrades() throws Exception {
        // Given
        Grade grade1 = Grade.builder().value(4).build();
        Grade grade2 = Grade.builder().value(5).build();
        Grade grade3 = Grade.builder().value(3).build();
        List<Grade> grades = Arrays.asList(grade1, grade2, grade3);

        // When - using reflection to access private method
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateAverageGrade", List.class);
        method.setAccessible(true);
        double result = (double) method.invoke(adminDashboardController, grades);

        // Then
        assertEquals(4.0, result, 0.01);
    }

    @Test
    void testCalculateAverageGrade_WithEmptyList() throws Exception {
        // Given
        List<Grade> grades = new ArrayList<>();

        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateAverageGrade", List.class);
        method.setAccessible(true);
        double result = (double) method.invoke(adminDashboardController, grades);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateAverageGrade_WithNullList() throws Exception {
        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateAverageGrade", List.class);
        method.setAccessible(true);
        double result = (double) method.invoke(adminDashboardController, (List<Grade>) null);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateGradeDistribution_WithValidGrades() throws Exception {
        // Given
        Grade grade1 = Grade.builder().value(4).build();
        Grade grade2 = Grade.builder().value(5).build();
        Grade grade3 = Grade.builder().value(4).build();
        List<Grade> grades = Arrays.asList(grade1, grade2, grade3);

        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateGradeDistribution", List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, Long> result = (Map<Integer, Long>) method.invoke(adminDashboardController, grades);

        // Then
        assertEquals(2, result.size());
        assertEquals(2L, result.get(4));
        assertEquals(1L, result.get(5));
    }

    @Test
    void testCalculateGradeDistribution_WithEmptyList() throws Exception {
        // Given
        List<Grade> grades = new ArrayList<>();

        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateGradeDistribution", List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, Long> result = (Map<Integer, Long>) method.invoke(adminDashboardController, grades);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateGradeDistribution_WithNullList() throws Exception {
        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateGradeDistribution", List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, Long> result = (Map<Integer, Long>) method.invoke(adminDashboardController, (List<Grade>) null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateSchoolStatistics_WithValidData() throws Exception {
        // Given
        List<Student> students = Arrays.asList(student);
        List<Grade> grades = Arrays.asList(grade);
        List<Absence> absences = Arrays.asList(absence);

        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateSchoolStatistics", 
                School.class, List.class, List.class, List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) method.invoke(adminDashboardController, 
                school, students, grades, absences);

        // Then
        assertNotNull(result);
        assertEquals(1, result.get("studentCount"));
        assertEquals(1, result.get("gradeCount"));
        assertEquals(1, result.get("absenceCount"));
        assertEquals(1L, result.get("schoolId"));
        assertTrue(result.containsKey("averageGrade"));
        assertTrue(result.containsKey("gradeDistribution"));
    }

    @Test
    void testCalculateSchoolStatistics_WithNullSchool() throws Exception {
        // Given
        List<Student> students = Arrays.asList(student);
        List<Grade> grades = Arrays.asList(grade);
        List<Absence> absences = Arrays.asList(absence);

        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateSchoolStatistics", 
                School.class, List.class, List.class, List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) method.invoke(adminDashboardController, 
                null, students, grades, absences);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateSchoolStatistics_WithNullData() throws Exception {
        // When
        Method method = AdminDashboardController.class.getDeclaredMethod("calculateSchoolStatistics", 
                School.class, List.class, List.class, List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) method.invoke(adminDashboardController, 
                school, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(0, result.get("studentCount"));
        assertEquals(0, result.get("gradeCount"));
        assertEquals(0, result.get("absenceCount"));
        assertEquals(1L, result.get("schoolId"));
    }

    @Test
    void testDashboard_WithMixedNullAndValidData() {
        // Given
        when(userService.findAllUsers()).thenReturn(Arrays.asList(user));
        when(schoolService.getAllSchools()).thenReturn(null);
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));
        when(teacherService.getAllTeachers()).thenReturn(null);
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));
        when(directorService.getAllDirectors()).thenReturn(null);
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAllAcademicTerms()).thenReturn(null);
        when(gradeService.getAll()).thenReturn(Arrays.asList(grade));
        when(absenceService.getAll()).thenReturn(null);

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 1);
        verify(model).addAttribute("totalSchools", 0);
        verify(model).addAttribute("totalStudents", 1);
        verify(model).addAttribute("totalTeachers", 0);
        verify(model).addAttribute("totalParents", 1);
        verify(model).addAttribute("totalDirectors", 0);
        verify(model).addAttribute("totalSubjects", 1);
        verify(model).addAttribute("totalAcademicTerms", 0);
        verify(model).addAttribute("totalGrades", 1);
        verify(model).addAttribute("totalAbsences", 0);
    }

    @Test
    void testDashboard_WithEmptyLists() {
        // Given
        when(userService.findAllUsers()).thenReturn(new ArrayList<>());
        when(schoolService.getAllSchools()).thenReturn(new ArrayList<>());
        when(studentService.getAllStudents()).thenReturn(new ArrayList<>());
        when(teacherService.getAllTeachers()).thenReturn(new ArrayList<>());
        when(parentService.getAllParents()).thenReturn(new ArrayList<>());
        when(directorService.getAllDirectors()).thenReturn(new ArrayList<>());
        when(subjectService.getAllSubjects()).thenReturn(new ArrayList<>());
        when(academicTermService.getAllAcademicTerms()).thenReturn(new ArrayList<>());
        when(gradeService.getAll()).thenReturn(new ArrayList<>());
        when(absenceService.getAll()).thenReturn(new ArrayList<>());

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalUsers", 0);
        verify(model).addAttribute("totalSchools", 0);
        verify(model).addAttribute("totalStudents", 0);
        verify(model).addAttribute("totalTeachers", 0);
        verify(model).addAttribute("totalParents", 0);
        verify(model).addAttribute("totalDirectors", 0);
        verify(model).addAttribute("totalSubjects", 0);
        verify(model).addAttribute("totalAcademicTerms", 0);
        verify(model).addAttribute("totalGrades", 0);
        verify(model).addAttribute("totalAbsences", 0);
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

        when(userService.findAllUsers()).thenReturn(Arrays.asList(user));
        when(schoolService.getAllSchools()).thenReturn(Arrays.asList(school));
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));
        when(directorService.getAllDirectors()).thenReturn(Arrays.asList(director));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAllAcademicTerms()).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getAll()).thenReturn(Arrays.asList(grade, gradeWithNullSubject));
        when(absenceService.getAll()).thenReturn(Arrays.asList(absence));

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalGrades", 2);
        verify(model).addAttribute(eq("subjectStats"), any(Map.class));
    }

    @Test
    void testDashboard_WithAbsencesContainingNullJustified() {
        // Given
        Absence absenceWithNullJustified = Absence.builder()
                .id(2L)
                .student(student)
                .absenceDate(LocalDate.now())
                .justified(null)
                .build();

        when(userService.findAllUsers()).thenReturn(Arrays.asList(user));
        when(schoolService.getAllSchools()).thenReturn(Arrays.asList(school));
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));
        when(parentService.getAllParents()).thenReturn(Arrays.asList(parent));
        when(directorService.getAllDirectors()).thenReturn(Arrays.asList(director));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(academicTermService.getAllAcademicTerms()).thenReturn(Arrays.asList(academicTerm));
        when(gradeService.getAll()).thenReturn(Arrays.asList(grade));
        when(absenceService.getAll()).thenReturn(Arrays.asList(absence, absenceWithNullJustified));

        // When
        String result = adminDashboardController.dashboard(model);

        // Then
        assertEquals("admin/dashboard", result);
        verify(model).addAttribute("totalAbsences", 2);
        verify(model).addAttribute("justifiedAbsences", 1L);
        verify(model).addAttribute("unjustifiedAbsences", 0L);
        verify(model).addAttribute("pendingAbsences", 1L);
    }

    @Test
    void testViewStatistics_WithGradesContainingNullStudentSchool() {
        // Given
        Student studentWithNullSchool = Student.builder()
                .id(2L)
                .school(null)
                .build();
        Grade gradeWithNullStudentSchool = Grade.builder()
                .id(2L)
                .value(4)
                .student(studentWithNullSchool)
                .subject(subject)
                .teacher(teacher)
                .dateAwarded(LocalDate.now())
                .build();

        when(gradeService.getAll()).thenReturn(Arrays.asList(grade, gradeWithNullStudentSchool));
        when(absenceService.getAll()).thenReturn(Arrays.asList(absence));
        when(schoolService.getAllSchools()).thenReturn(Arrays.asList(school));
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subject));
        when(studentService.getStudentsBySchoolId(anyLong())).thenReturn(Arrays.asList(student));
        when(gradeService.getGradesByStudentId(anyLong())).thenReturn(Arrays.asList(grade));
        when(absenceService.getAbsencesByStudentId(anyLong())).thenReturn(Arrays.asList(absence));

        // When
        String result = adminDashboardController.viewStatistics(model);

        // Then
        assertEquals("admin/statistics", result);
        verify(model).addAttribute("totalGrades", 2);
        verify(model).addAttribute(eq("globalSubjectStats"), any(Map.class));
    }
} 