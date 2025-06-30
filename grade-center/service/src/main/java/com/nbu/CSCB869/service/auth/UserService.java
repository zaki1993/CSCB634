package com.nbu.CSCB869.service.auth;

import com.nbu.CSCB869.model.auth.User;
import com.nbu.CSCB869.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUserName(String username) {
        return userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("username " + username + " does not exist"));
    }
}
