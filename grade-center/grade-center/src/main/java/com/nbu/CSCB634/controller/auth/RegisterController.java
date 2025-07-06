package com.nbu.CSCB634.controller.auth;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.auth.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // This refers to register.html in your templates
    }

    @PostMapping("/register")
    public String processRegister(@Valid User user, BindingResult bindingResult,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username is already taken.");
            return "register";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email is already taken.");
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role
        if (user.getRole() == null) {
            user.setRole(Role.NONE);
        }

        userRepository.save(user);

        // Redirect to login page after successful registration
        return "redirect:/login";
    }
}
