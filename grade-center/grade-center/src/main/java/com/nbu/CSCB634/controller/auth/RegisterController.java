package com.nbu.CSCB634.controller.auth;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.auth.UserService;
import com.nbu.CSCB634.service.exceptions.UserAlreadyExistException;
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
    private UserService userService;

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

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        try {
            userService.registerUser(user);
        } catch (UserAlreadyExistException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        // Redirect to login page after successful registration
        return "redirect:/login";
    }
}
