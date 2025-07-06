package com.nbu.CSCB634.controller.admin;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.service.auth.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/updateRole")
    public String updateRole(@RequestParam Long userId, @RequestParam Role role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/users";
    }
}
