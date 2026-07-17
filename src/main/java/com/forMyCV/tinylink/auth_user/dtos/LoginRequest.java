package com.forMyCV.tinylink.auth_user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email can't be empty")
    @Email(message = "email invalid format")
    String email;
    @NotBlank(message = "password can't be empty")
    String password;
}
