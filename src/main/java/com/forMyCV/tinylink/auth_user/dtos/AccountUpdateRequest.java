package com.forMyCV.tinylink.auth_user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AccountUpdateRequest {

    private String name;
    private String email;
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String address;

    private MultipartFile profilePicture;
}
