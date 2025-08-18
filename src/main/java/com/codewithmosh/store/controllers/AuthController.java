package com.codewithmosh.store.controllers;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.dtos.JwtResponse;
import com.codewithmosh.store.dtos.LoginDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.exceptions.PasswordInvalidException;
import com.codewithmosh.store.exceptions.UserNotFoundException;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.AuthService;
import com.codewithmosh.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        ); //authenticationManager takes the code from UserService that is used to validate the user exists with email = loginDto email
        //also will check the password as well, meaning all logic is handled in spring boot security internally
        var user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var cookie = new Cookie("refreshToken", refreshToken.toString()); //HttpOnly cookie used as it is less exposed than sending through the body
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh"); //sets endpoint for accessing refresh tokens
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); //7 days
        cookie.setSecure(true); //only expose through http connections
        response.addCookie(cookie); //sets the secure cookie through HTTPSerlvetResponse
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        /*
        var authUser = SecurityContextHolder.getContext().getAuthentication(); //returns authentication object which is information about the current authenticated user
        var authId = (Long) authUser.getPrincipal(); //returns anything, however principal is email from the JwtAuthenticationFilter class
        var user = userRepository.findById(authId).orElse(null);
        */
        var user = authService.returnAuthUser();
        //var user = userRepository.findByEmail(authEmail).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken) {
        var jwt = jwtService.parse(refreshToken);
        if(jwt == null || !jwt.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            var userId = jwt.getUserIdFromToken();
            var user =  userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            var accessToken = jwtService.generateAccessToken(user);
            return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
        }
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User was not found"));
    }
    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Password is not valid"));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
