package com.nbu.CSCB634.global;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Helper class to check if the current logged-in user is teacher or student
 */
public class AccessControlConfig {
    /**
     * Return if the current user is admin
     * @return
     */
    public static boolean isAdmin() {
        return hasRole("ADMINISTRATOR");
    }

    /**
     * Return if the current user is teacher
     * @return
     */
    public static boolean isTeacher() {
        return hasRole("TEACHER");
    }

    /**
     * Return if the current user is student
     * @return
     */
    public static boolean isStudent() {
        return hasRole("STUDENT");
    }

    /**
     * Return if the current user is parent
     * @return
     */
    public static boolean isParent() {
        return hasRole("PARENT");
    }

    /**
     * Return if the current user is director
     * @return
     */
    public static boolean isDirector() {
        return hasRole("DIRECTOR");
    }

    /**
     * Returns the username of the current logged-in user.
     *
     * @return
     */
    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
    /**
     * Check if the current user has role
     *
     * @param role to check
     * @return
     */
    private static boolean hasRole(String role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}