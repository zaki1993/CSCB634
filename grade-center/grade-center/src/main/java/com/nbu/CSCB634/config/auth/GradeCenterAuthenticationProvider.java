package com.nbu.CSCB634.config.auth;

import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GradeCenterAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        try {
            User user = userService.findByUsername(username).orElse(null);
            if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Wrong username or password");
            }
            List authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
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
