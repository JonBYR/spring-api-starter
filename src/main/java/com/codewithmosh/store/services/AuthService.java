package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.LoginDto;
import com.codewithmosh.store.exceptions.PasswordInvalidException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public void login(LoginDto loginDto) {
        var u = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
        if (u == null) {
            throw new UserNotFoundException();
        }
        if(!passwordEncoder.matches(loginDto.getPassword(), u.getPassword())) {
            throw new PasswordInvalidException();
        }
    }
}
