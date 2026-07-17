package com.forMyCV.tinylink.auth_user.controller;

import com.forMyCV.tinylink.auth_user.dtos.AccountUpdateRequest;
import com.forMyCV.tinylink.auth_user.dtos.LoginRequest;
import com.forMyCV.tinylink.auth_user.dtos.UserDto;
import com.forMyCV.tinylink.auth_user.services.UserService;
import com.forMyCV.tinylink.response.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<Response<UserDto>> getOwnAccountDetails() {
        return new  ResponseEntity<>(userService.getOwnAccountDetails(), HttpStatus.OK);
    }
    @PutMapping(value = "/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<?>> updateUser(@ModelAttribute @Valid
                                                      AccountUpdateRequest accountUpdateRequest,
                                                  @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        accountUpdateRequest.setProfilePicture(profilePicture);
        return new ResponseEntity<>(userService.updateOwnAccount(accountUpdateRequest), HttpStatus.OK);
    }

    @DeleteMapping("/deactive")
    public ResponseEntity<Response<?>> deactivateAccount() {
        return new ResponseEntity<>(userService.deactivateAccount(), HttpStatus.OK);
    }
}
