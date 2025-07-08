package com.nbu.CSCB634.service.auth;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.auth.UserRepository;
import com.nbu.CSCB634.service.exceptions.UserAlreadyExistException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(@Valid User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistException("Username already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistException("Email already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default role STUDENT for example or depending on registration type
        if (user.getRole() == null) {
            user.setRole(Role.NONE);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setRole(role);
        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Long getNextAvailableId() {
        // Намери следващото свободно ID
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return 1L;
        }
        
        Long maxId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        
        return maxId + 1;
    }
}