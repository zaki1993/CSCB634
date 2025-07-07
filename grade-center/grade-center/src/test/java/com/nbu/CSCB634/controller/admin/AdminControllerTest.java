package com.nbu.CSCB634.controller.admin;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.AcademicTermService;
import com.nbu.CSCB634.service.SchoolClassService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.SubjectService;
import com.nbu.CSCB634.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SchoolService schoolService;

    @Mock
    private SchoolClassService classService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private AcademicTermService termService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    private User user;
    private School school;
    private SchoolClass schoolClass;
    private Subject subject;
    private AcademicTerm academicTerm;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .role(Role.STUDENT)
                .build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("Test Address")
                .build();

        schoolClass = SchoolClass.builder()
                .id(1L)
                .name("10A")
                .gradeNumber(10)
                .school(school)
                .build();

        subject = Subject.builder()
                .id(1L)
                .name("Mathematics")
                .build();

        academicTerm = AcademicTerm.builder()
                .id(1L)
                .name("Term 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .school(school)
                .build();
    }

    // ==================== USER MANAGEMENT TESTS ====================

    @Test
    void testListUsers_Success() throws Exception {
        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).findAll();
    }

    @Test
    void testListUsers_EmptyList() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).findAll();
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        doNothing().when(userService).updateUserRole(anyLong(), any(Role.class));

        mockMvc.perform(post("/admin/updateRole")
                .param("userId", "1")
                .param("role", "TEACHER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).updateUserRole(1L, Role.TEACHER);
    }

    @Test
    void testUpdateRole_WithDifferentRoles() throws Exception {
        Role[] roles = {Role.ADMINISTRATOR, Role.DIRECTOR, Role.TEACHER, Role.PARENT, Role.STUDENT};

        for (Role role : roles) {
            doNothing().when(userService).updateUserRole(anyLong(), any(Role.class));

            mockMvc.perform(post("/admin/updateRole")
                    .param("userId", "1")
                    .param("role", role.name()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/users"));
        }

        verify(userService, times(roles.length)).updateUserRole(anyLong(), any(Role.class));
    }

    // ==================== SCHOOL MANAGEMENT TESTS ====================

    @Test
    void testListSchools_Success() throws Exception {
        when(schoolService.getAll()).thenReturn(List.of(school));

        mockMvc.perform(get("/admin/schools"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schools"))
                .andExpect(model().attributeExists("schools"));

        verify(schoolService, times(1)).getAll();
    }

    @Test
    void testCreateSchool_Success() throws Exception {
        when(schoolService.createSchool(any(School.class))).thenReturn(school);

        mockMvc.perform(post("/admin/schools/create")
                .param("name", "Test School")
                .param("address", "Test Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schools"));

        verify(schoolService, times(1)).createSchool(any(School.class));
    }

    @Test
    void testDeleteSchool_Success() throws Exception {
        doNothing().when(schoolService).deleteSchool(anyLong());

        mockMvc.perform(post("/admin/schools/delete")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schools"));

        verify(schoolService, times(1)).deleteSchool(1L);
    }

    // ==================== CLASS MANAGEMENT TESTS ====================

    @Test
    void testListClasses_Success() throws Exception {
        when(classService.getAll()).thenReturn(List.of(schoolClass));
        when(schoolService.getAll()).thenReturn(List.of(school));

        mockMvc.perform(get("/admin/classes"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/classes"))
                .andExpect(model().attributeExists("classes"))
                .andExpect(model().attributeExists("schools"));

        verify(classService, times(1)).getAll();
        verify(schoolService, times(1)).getAll();
    }

    @Test
    void testCreateClass_Success() throws Exception {
        when(classService.createSchoolClass(any(SchoolClass.class))).thenReturn(schoolClass);

        mockMvc.perform(post("/admin/classes/create")
                .param("name", "10A")
                .param("gradeNumber", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes"));

        verify(classService, times(1)).createSchoolClass(any(SchoolClass.class));
    }

    @Test
    void testDeleteClass_Success() throws Exception {
        doNothing().when(classService).deleteSchoolClass(anyLong());

        mockMvc.perform(post("/admin/classes/delete")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes"));

        verify(classService, times(1)).deleteSchoolClass(1L);
    }

    // ==================== SUBJECT MANAGEMENT TESTS ====================

    @Test
    void testListSubjects_Success() throws Exception {
        when(subjectService.getAll()).thenReturn(List.of(subject));

        mockMvc.perform(get("/admin/subjects"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subjects"))
                .andExpect(model().attributeExists("subjects"));

        verify(subjectService, times(1)).getAll();
    }

    @Test
    void testCreateSubject_Success() throws Exception {
        when(subjectService.createSubject(any(Subject.class))).thenReturn(subject);

        mockMvc.perform(post("/admin/subjects/create")
                .param("name", "Mathematics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects"));

        verify(subjectService, times(1)).createSubject(any(Subject.class));
    }

    @Test
    void testDeleteSubject_Success() throws Exception {
        doNothing().when(subjectService).deleteSubject(anyLong());

        mockMvc.perform(post("/admin/subjects/delete")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects"));

        verify(subjectService, times(1)).deleteSubject(1L);
    }

    // ==================== ACADEMIC TERM MANAGEMENT TESTS ====================

    @Test
    void testListTerms_Success() throws Exception {
        when(termService.getAll()).thenReturn(List.of(academicTerm));
        when(schoolService.getAll()).thenReturn(List.of(school));
        when(subjectService.getAll()).thenReturn(List.of(subject));

        mockMvc.perform(get("/admin/terms"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/terms"))
                .andExpect(model().attributeExists("terms"))
                .andExpect(model().attributeExists("schools"))
                .andExpect(model().attributeExists("subjects"));

        verify(termService, times(1)).getAll();
        verify(schoolService, times(1)).getAll();
        verify(subjectService, times(1)).getAll();
    }

    @Test
    void testCreateTerm_Success() throws Exception {
        when(termService.createAcademicTerm(any(AcademicTerm.class))).thenReturn(academicTerm);

        mockMvc.perform(post("/admin/terms/create")
                .param("name", "Term 1")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-06-30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/terms"));

        verify(termService, times(1)).createAcademicTerm(any(AcademicTerm.class));
    }

    @Test
    void testDeleteTerm_Success() throws Exception {
        doNothing().when(termService).deleteAcademicTerm(anyLong());

        mockMvc.perform(post("/admin/terms/delete")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/terms"));

        verify(termService, times(1)).deleteAcademicTerm(1L);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    void testUpdateRole_InvalidUserId() throws Exception {
        mockMvc.perform(post("/admin/updateRole")
                .param("userId", "invalid")
                .param("role", "TEACHER"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSchool_InvalidId() throws Exception {
        mockMvc.perform(post("/admin/schools/delete")
                .param("id", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteClass_InvalidId() throws Exception {
        mockMvc.perform(post("/admin/classes/delete")
                .param("id", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSubject_InvalidId() throws Exception {
        mockMvc.perform(post("/admin/subjects/delete")
                .param("id", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTerm_InvalidId() throws Exception {
        mockMvc.perform(post("/admin/terms/delete")
                .param("id", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSchool_WithEmptyName() throws Exception {
        when(schoolService.createSchool(any(School.class))).thenReturn(school);

        mockMvc.perform(post("/admin/schools/create")
                .param("name", "")
                .param("address", "Test Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schools"));

        verify(schoolService, times(1)).createSchool(any(School.class));
    }

    @Test
    void testCreateSubject_WithEmptyName() throws Exception {
        when(subjectService.createSubject(any(Subject.class))).thenReturn(subject);

        mockMvc.perform(post("/admin/subjects/create")
                .param("name", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects"));

        verify(subjectService, times(1)).createSubject(any(Subject.class));
    }
    @Test
    void testCreateTerm_WithInvalidDateFormat() throws Exception {
        mockMvc.perform(post("/admin/terms/create")
                .param("name", "Term 1")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-06-30"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRole_WithNullRole() throws Exception {
        mockMvc.perform(post("/admin/updateRole")
                .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRole_WithNullUserId() throws Exception {
        mockMvc.perform(post("/admin/updateRole")
                .param("role", "TEACHER"))
                .andExpect(status().isBadRequest());
    }
} 