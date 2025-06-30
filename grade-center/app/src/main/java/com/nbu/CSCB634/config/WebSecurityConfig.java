package com.nbu.CSCB634.config;

import com.nbu.CSCB634.config.auth.GraduationSystemAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .authorizeHttpRequests(authorize ->
                    authorize.requestMatchers("/login", "/css/**", "/js/**").permitAll()  // Allow login and static resources
                            .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(login -> login.loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll())
            .logout(logout -> logout.logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())
                .authenticationProvider(new GraduationSystemAuthenticationProvider());

        return http.build();
    }
}
