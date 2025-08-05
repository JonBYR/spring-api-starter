package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.LoginDto;
import com.codewithmosh.store.exceptions.PasswordInvalidException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginDto loginDto) {
        authService.login(loginDto);
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User was not found"));
    }
    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Password is not valid"));
    }
}
