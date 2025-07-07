package com.nbu.CSCB634.service.auth;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.auth.UserRepository;
import com.nbu.CSCB634.service.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("plainpassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(passwordEncoder, times(1)).encode("plainpassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_WithNullRole() {
        user.setRole(null);
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.NONE);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindByUsername_Found() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testFindAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.findAll();

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(99L);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void testDeleteById_Success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateUserRole_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserRole(1L, Role.TEACHER);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserRole_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserRole(99L, Role.TEACHER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with ID: 99");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSave_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetNextAvailableId_EmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        Long result = userService.getNextAvailableId();

        assertThat(result).isEqualTo(1L);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetNextAvailableId_WithExistingUsers() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(5L).build();
        User user3 = User.builder().id(3L).build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        Long result = userService.getNextAvailableId();

        assertThat(result).isEqualTo(6L);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetNextAvailableId_WithSingleUser() {
        User singleUser = User.builder().id(10L).build();

        when(userRepository.findAll()).thenReturn(List.of(singleUser));

        Long result = userService.getNextAvailableId();

        assertThat(result).isEqualTo(11L);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testRegisterUser_PasswordEncodingVerification() {
        String plainPassword = "testPassword123";
        String encodedPassword = "encodedPassword123";
        
        user.setPassword(plainPassword);
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.registerUser(user);

        verify(passwordEncoder, times(1)).encode(plainPassword);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    void testRegisterUser_WithExistingRole() {
        user.setRole(Role.ADMINISTRATOR);
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertThat(result.getRole()).isEqualTo(Role.ADMINISTRATOR);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserRole_AllRoles() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Test all roles
        Role[] roles = {Role.ADMINISTRATOR, Role.DIRECTOR, Role.TEACHER, Role.PARENT, Role.STUDENT, Role.NONE};
        
        for (Role role : roles) {
            userService.updateUserRole(1L, role);
            assertThat(user.getRole()).isEqualTo(role);
        }

        verify(userRepository, times(roles.length)).save(user);
    }
} 