package com.nbu.CSCB634.controller;

import com.nbu.CSCB634.model.Role;
import com.nbu.CSCB634.service.RoleChangeService;
import com.nbu.CSCB634.service.SchoolService;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/role-change")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class RoleChangeController {

    private final RoleChangeService roleChangeService;
    private final UserService userService;
    private final SchoolService schoolService;

    /**
     * Показва форма за смяна на роля
     */
    @GetMapping("/{userId}")
    public String showRoleChangeForm(@PathVariable Long userId, Model model) {
        var user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен"));
        
        var roleInfo = roleChangeService.getCurrentRoleInfo(userId);
        var schools = schoolService.getAllSchools();
        
        model.addAttribute("user", user);
        model.addAttribute("roleInfo", roleInfo);
        model.addAttribute("schools", schools);
        model.addAttribute("availableRoles", Role.values());
        
        return "admin/role-change";
    }

    /**
     * Валидира смяната на роля (AJAX)
     */
    @PostMapping("/{userId}/validate")
    @ResponseBody
    public RoleChangeService.RoleChangeValidation validateRoleChange(
            @PathVariable Long userId, 
            @RequestParam Role newRole,
            @RequestParam(required = false) Long schoolId) {
        
        var request = new RoleChangeService.RoleChangeRequest();
        request.setSchoolId(schoolId);
        
        return roleChangeService.validateRoleChange(userId, newRole, request);
    }

    /**
     * Изпълнява смяната на роля
     */
    @PostMapping("/{userId}/execute")
    public String executeRoleChange(@PathVariable Long userId,
                                   @RequestParam Role newRole,
                                   @RequestParam(required = false) Long schoolId,
                                   RedirectAttributes redirectAttributes) {
        try {
            var request = new RoleChangeService.RoleChangeRequest();
            request.setSchoolId(schoolId);
            
            var user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен"));
            
            Role oldRole = user.getRole();
            
            roleChangeService.changeUserRole(userId, newRole, request);
            
            redirectAttributes.addFlashAttribute("success", 
                    "Успешно сменихте ролята на " + user.getUsername() + 
                    " от " + oldRole + " на " + newRole);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                    "Грешка при смяна на роля: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * REST endpoint за получаване на информация за роля
     */
    @GetMapping("/{userId}/info")
    @ResponseBody
    public RoleChangeService.RoleInfo getRoleInfo(@PathVariable Long userId) {
        return roleChangeService.getCurrentRoleInfo(userId);
    }
} 