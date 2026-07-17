package com.forMyCV.tinylink.auth_user.services;


import com.forMyCV.tinylink.auth_user.dtos.LoginRequest;
import com.forMyCV.tinylink.auth_user.dtos.LoginResponse;
import com.forMyCV.tinylink.auth_user.dtos.RegistrationRequest;
import com.forMyCV.tinylink.auth_user.entity.User;
import com.forMyCV.tinylink.auth_user.repository.UserRepository;
import com.forMyCV.tinylink.exceptions.BusinessException;
import com.forMyCV.tinylink.exceptions.ErrorCode;
import com.forMyCV.tinylink.response.Response;
import com.forMyCV.tinylink.role.entity.Role;
import com.forMyCV.tinylink.role.repositories.RoleRepository;
import com.forMyCV.tinylink.security.JwtUtils;
import com.forMyCV.tinylink.security.MyUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils  jwtUtils;

    @Value("${user.default.role}")
    private String DEFAULT_ROLE;


    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {

        log.info("INSIDE register()");

        //check if email exists.
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        List<Role> roles;
        if (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()) {
            roles = registrationRequest.getRoles()
                    .stream()
                    .map(role -> roleRepository.findByRoleName(role.toUpperCase())
                            .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setRoleName(role.toUpperCase());
                                return roleRepository.save(newRole);
                            })).toList();
        } else {
            Role defaultRole = roleRepository.findByRoleName(DEFAULT_ROLE)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(DEFAULT_ROLE);
                        return roleRepository.save(role);
                    });

            roles = List.of(defaultRole);
        }

        User user = User.builder()
                .username(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .address(registrationRequest.getPhoneNumber())
                .address(registrationRequest.getAddress())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(roles)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        log.info("user registered successfully");

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("user registered successfully")
                .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_CREDENTIALS));


        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_NON_ACTIVE);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        //generate token
        MyUserDetails myUserDetails = MyUserDetails.builder()
                .user(user)
                .build();


        String token =  jwtUtils.generateToken(myUserDetails);

        List<String> roles = user.getRoles()
                .stream().map(Role::getRoleName)
                .toList();

        LoginResponse loginResponse = new  LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRoles(roles);

        return  Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }
}
