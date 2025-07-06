package com.nbu.CSCB634.service.auth;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.auth.UserRepository;
import com.nbu.CSCB634.service.exceptions.UserAlreadyExistException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}