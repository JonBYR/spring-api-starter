package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.dtos.UpdateUserDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @GetMapping //don't need to specify the root as request mapping states root is /users
    //name is the name of the param in the request, in case paramater in the method is not called sort
    public Iterable<UserDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sort) {
        if(!Set.of("name", "email").contains(sort)) {
            sort = "name";
        }
        return userRepository.findAll(Sort.by(sort)).stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
                .toList();

    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build(); //returns 404
        }
        else {
            var userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
            return ResponseEntity.ok(userDto); //return 200
        }
    }
    @PostMapping//valid annotation checks that user is valid using jakarta.validation errors
    public ResponseEntity<?> createUser( //method returns either a UserDto or a Map
            @Valid @RequestBody RegisterUserDto userDto,
            UriComponentsBuilder uriBuilder
    ) {
        if(userRepository.existsByEmail(userDto.getEmail())) { //validation of business rules
            return ResponseEntity.badRequest().body(
                    Map.of("email", "email is already entered")
            );
        }
        var user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); //encode password to a hash
        user.setRole(Role.USER); //could be done in a mapper
        userRepository.save(user);
        UserDto dTo = userMapper.toDto(user);
        //uri builder is needed to showcase where the resource has been created for 201 response, in the response header
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(dTo.getId()).toUri();
        return ResponseEntity.created(uri).body(dTo);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "id") Long id, @RequestBody UpdateUserDto userDto) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build(); //returns 404 error
        }
        else {
            userMapper.update(userDto, user);
            userRepository.save(user); //ensures updated changes are saved to the database
            return ResponseEntity.ok(userMapper.toDto(user));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            userRepository.delete(user);
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable(name = "id") Long id, @RequestBody ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if(user.getPassword().equals(request.getOldPassword())) {
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
