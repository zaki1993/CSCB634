package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.*;
import com.nbu.CSCB634.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleChangeServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AdministratorRepository administratorRepository;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SchoolRepository schoolRepository;
    @Mock
    private SchoolService schoolService;

    @InjectMocks
    private RoleChangeService roleChangeService;

    private User user;
    private School school;
    private Teacher teacher;
    private Student student;
    private Director director;
    private Parent parent;

    @BeforeEach
    void setUp() {
        // Създаваме тестови данни
        user = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role(Role.TEACHER)
                .build();

        school = School.builder()
                .id(1L)
                .name("Test School")
                .address("Test Address")
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .user(user)
                .school(school)
                .qualifiedSubjects(new HashSet<>())
                .grades(new ArrayList<>())
                .absences(new ArrayList<>())
                .build();

        student = Student.builder()
                .id(1L)
                .user(user)
                .school(school)
                .parents(new ArrayList<>())
                .grades(new ArrayList<>())
                .absences(new ArrayList<>())
                .build();

        director = Director.builder()
                .id(1L)
                .user(user)
                .school(school)
                .build();

        parent = Parent.builder()
                .id(1L)
                .user(user)
                .students(new HashSet<>())
                .build();
    }

    @Test
    void changeUserRole_FromTeacherToDirector_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(schoolService.getSchoolById(1L)).thenReturn(Optional.of(school));
        when(userRepository.save(any(User.class))).thenReturn(user);

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        request.setSchoolId(1L);

        // Act
        roleChangeService.changeUserRole(1L, Role.DIRECTOR, request);

        // Assert
        verify(teacherRepository).deleteById(1L);
        verify(directorRepository).save(any(Director.class));
        verify(userRepository).save(user);
        assertEquals(Role.DIRECTOR, user.getRole());
    }

    @Test
    void changeUserRole_FromStudentToParent_Success() {
        // Arrange
        user.setRole(Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();

        // Act
        roleChangeService.changeUserRole(1L, Role.PARENT, request);

        // Assert
        verify(studentRepository).deleteById(1L);
        verify(parentRepository).save(any(Parent.class));
        verify(userRepository).save(user);
        assertEquals(Role.PARENT, user.getRole());
    }

    @Test
    void changeUserRole_ToDirectorWithoutSchool_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        // request.setSchoolId(null); // Не задаваме училище

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> roleChangeService.changeUserRole(1L, Role.DIRECTOR, request));
    }

    @Test
    void changeUserRole_ToTeacherWithoutSchool_ThrowsException() {
        // Arrange
        user.setRole(Role.PARENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        // request.setSchoolId(null); // Не задаваме училище

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> roleChangeService.changeUserRole(1L, Role.TEACHER, request));
    }

    @Test
    void changeUserRole_ToStudentWithoutSchool_ThrowsException() {
        // Arrange
        user.setRole(Role.PARENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        // request.setSchoolId(null); // Не задаваме училище

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> roleChangeService.changeUserRole(1L, Role.STUDENT, request));
    }

    @Test
    void changeUserRole_SameRole_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        request.setSchoolId(1L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> roleChangeService.changeUserRole(1L, Role.TEACHER, request));
    }

    @Test
    void changeUserRole_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> roleChangeService.changeUserRole(1L, Role.DIRECTOR, request));
    }

    @Test
    void validateRoleChange_FromTeacherToDirector_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        request.setSchoolId(1L);

        // Act
        RoleChangeService.RoleChangeValidation validation = 
                roleChangeService.validateRoleChange(1L, Role.DIRECTOR, request);

        // Assert
        assertTrue(validation.isValid());
        assertEquals(0, validation.getErrors().size());
    }

    @Test
    void validateRoleChange_ToDirectorWithoutSchool_Invalid() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        // request.setSchoolId(null); // Не задаваме училище

        // Act
        RoleChangeService.RoleChangeValidation validation = 
                roleChangeService.validateRoleChange(1L, Role.DIRECTOR, request);

        // Assert
        assertFalse(validation.isValid());
        assertEquals(1, validation.getErrors().size());
        assertTrue(validation.getErrors().get(0).contains("Училището е задължително"));
    }

    @Test
    void validateRoleChange_SameRole_Invalid() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        request.setSchoolId(1L);

        // Act
        RoleChangeService.RoleChangeValidation validation = 
                roleChangeService.validateRoleChange(1L, Role.TEACHER, request);

        // Assert
        assertFalse(validation.isValid());
        assertEquals(1, validation.getErrors().size());
        assertTrue(validation.getErrors().get(0).contains("вече има тази роля"));
    }

    @Test
    void validateRoleChange_TeacherWithData_HasWarnings() {
        // Arrange
        teacher.getGrades().add(new Grade()); // Добавяме една оценка
        teacher.getAbsences().add(new Absence()); // Добавяме едно отсъствие
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        RoleChangeService.RoleChangeRequest request = new RoleChangeService.RoleChangeRequest();
        request.setSchoolId(1L);

        // Act
        RoleChangeService.RoleChangeValidation validation = 
                roleChangeService.validateRoleChange(1L, Role.DIRECTOR, request);

        // Assert
        assertTrue(validation.isValid());
        assertEquals(0, validation.getErrors().size());
        assertTrue(validation.getWarnings().size() > 0);
        assertTrue(validation.getWarnings().stream().anyMatch(w -> w.contains("оценки")));
        assertTrue(validation.getWarnings().stream().anyMatch(w -> w.contains("отсъствия")));
    }

    @Test
    void getCurrentRoleInfo_Teacher_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        RoleChangeService.RoleInfo roleInfo = roleChangeService.getCurrentRoleInfo(1L);

        // Assert
        assertNotNull(roleInfo);
        assertEquals(1L, roleInfo.getUserId());
        assertEquals(Role.TEACHER, roleInfo.getCurrentRole());
        assertEquals("testuser", roleInfo.getUsername());
        assertEquals("Test User", roleInfo.getFullName());
        assertEquals(1L, roleInfo.getSchoolId());
        assertEquals("Test School", roleInfo.getSchoolName());
    }

    @Test
    void getCurrentRoleInfo_Student_Success() {
        // Arrange
        user.setRole(Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        RoleChangeService.RoleInfo roleInfo = roleChangeService.getCurrentRoleInfo(1L);

        // Assert
        assertNotNull(roleInfo);
        assertEquals(1L, roleInfo.getUserId());
        assertEquals(Role.STUDENT, roleInfo.getCurrentRole());
        assertEquals("testuser", roleInfo.getUsername());
        assertEquals("Test User", roleInfo.getFullName());
        assertEquals(1L, roleInfo.getSchoolId());
        assertEquals("Test School", roleInfo.getSchoolName());
    }

    @Test
    void getCurrentRoleInfo_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> roleChangeService.getCurrentRoleInfo(1L));
    }
} 