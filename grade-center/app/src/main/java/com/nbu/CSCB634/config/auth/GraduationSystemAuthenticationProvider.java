package com.nbu.CSCB634.config.auth;

import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GraduationSystemAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        try {
            User user = userService.getUserByUserName(username);
            if (user == null || !user.getPassword().equals(password)) {
                throw new BadCredentialsException("Wrong username or password");
            }
            List authorities = List.of(new SimpleGrantedAuthority(user.getUserType().toString()));
            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
        } catch (Exception e) {
            log.trace("Authentication failed", e);
            throw new BadCredentialsException("Wrong username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
