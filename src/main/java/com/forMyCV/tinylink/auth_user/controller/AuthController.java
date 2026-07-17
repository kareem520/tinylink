package com.forMyCV.tinylink.auth_user.controller;


import com.forMyCV.tinylink.auth_user.dtos.LoginRequest;
import com.forMyCV.tinylink.auth_user.dtos.LoginResponse;
import com.forMyCV.tinylink.auth_user.dtos.RegistrationRequest;
import com.forMyCV.tinylink.auth_user.services.AuthService;
import com.forMyCV.tinylink.response.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<?>> register(@RequestBody @Valid RegistrationRequest registrationRequest){

        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
