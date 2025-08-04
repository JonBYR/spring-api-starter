package com.codewithmosh.store.dtos;

import com.codewithmosh.store.validation.Lowercase;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegisterUserDto {
    @NotBlank(message = "Name is required") //ensures string is not empty or only whitespaces
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid") //inbuilt verification to check it is in proper email syntax
    @Lowercase(message = "Email must be lowercase") //overridden message
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
