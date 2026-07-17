package com.forMyCV.tinylink.auth_user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationRequest {
    @NotBlank(message = "name can't be empty")
    @Size(
            min = 3,
            max = 50,
            message = "characters must be from 3 to 50 characters"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "Name can contain only letters, spaces, apostrophes (') and hyphens (-)"
    )
    private String name;

    @NotBlank(message = "email can't be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "phone number can't be empty")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "VALIDATION.REGISTRATION.PHONE.FORMAT"
    )
    private String phoneNumber;

    @NotBlank(message = "address can't be empty")
    @Size(
            min = 3,
            max = 50,
            message = "VALIDATION.REGISTRATION.ADDRESS.SIZE,(3->50)"
    )
    private String address;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.BLANK")
    @Size(min = 8,
            max = 70,
            message = "VALIDATION.REGISTRATION.PASSWORD.SIZE (8->70)"
    )
    private String password;


    private List<String> roles;
}
