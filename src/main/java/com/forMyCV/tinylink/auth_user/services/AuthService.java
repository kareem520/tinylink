package com.forMyCV.tinylink.auth_user.services;

import com.forMyCV.tinylink.auth_user.dtos.LoginRequest;
import com.forMyCV.tinylink.auth_user.dtos.LoginResponse;
import com.forMyCV.tinylink.auth_user.dtos.RegistrationRequest;
import com.forMyCV.tinylink.response.Response;

public interface AuthService {

    public Response<?>register(RegistrationRequest registrationRequest);
    public Response<LoginResponse>login(LoginRequest loginRequest);

}
