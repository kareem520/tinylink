package com.forMyCV.tinylink.auth_user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;

    private List<String> roles;
}
