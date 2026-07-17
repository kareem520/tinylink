package com.forMyCV.tinylink.auth_user.services;

import com.forMyCV.tinylink.auth_user.dtos.AccountUpdateRequest;
import com.forMyCV.tinylink.auth_user.dtos.UserDto;
import com.forMyCV.tinylink.auth_user.entity.User;
import com.forMyCV.tinylink.auth_user.repository.UserRepository;
import com.forMyCV.tinylink.aws.AwsS3Service;
import com.forMyCV.tinylink.exceptions.BusinessException;
import com.forMyCV.tinylink.exceptions.ErrorCode;
import com.forMyCV.tinylink.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URL;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AwsS3Service awsS3Service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentLoggedInUser() {

        log.info("INSIDE getCurrentLoggedInUser()");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_EMAIL_NOT_FOUND));
    }

    @Override
    public Response<UserDto> getOwnAccountDetails() {
        log.info("INSIDE getOwnAccountDetails()");

        User user = getCurrentLoggedInUser();

        UserDto userDTO = modelMapper.map(user, UserDto.class);

        return Response.<UserDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Own account retrieved successfully")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<?> updateOwnAccount(AccountUpdateRequest accountUpdateRequest) {
        log.info("INSIDE updateOwnAccount");

        //fetch the LoggedIn User
        User user = getCurrentLoggedInUser();

        if (accountUpdateRequest.getEmail() != null && !accountUpdateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(accountUpdateRequest.getEmail())) {
                throw  new BusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(accountUpdateRequest.getEmail());
        }

        if (accountUpdateRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(accountUpdateRequest.getPassword()));
        if  (accountUpdateRequest.getName() != null) user.setName(accountUpdateRequest.getName());
        if (accountUpdateRequest.getPhoneNumber() != null) user.setPhoneNumber(accountUpdateRequest.getPhoneNumber());
        if (accountUpdateRequest.getAddress() != null) user.setAddress(accountUpdateRequest.getAddress());



        String profileUrl = user.getProfileUrl();

        MultipartFile profilePicture = accountUpdateRequest.getProfilePicture();

        if (profilePicture != null && !profilePicture.isEmpty()) {
            //delete the current profile picture from cloud if it exists
            if (profileUrl != null && !profileUrl.isEmpty()) {
                String keyName = profileUrl.substring(profileUrl.lastIndexOf("/") + 1);
                awsS3Service.deleteFile("profile/"+keyName);
                log.info("profile url deleted from s3 successfully");
            }
            //upload the new image
            String imageName = UUID.randomUUID().toString() + "_" + profilePicture.getOriginalFilename();
            URL newPictureUrl = awsS3Service.uploadFile("profile/"+imageName, profilePicture);
            user.setProfileUrl(newPictureUrl.toString());
        }


        userRepository.save(user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account Updated successfully")
                .build();
    }

    @Override
    public Response<?> deactivateAccount() {
        log.info("INSIDE deactivateAccount");

        User user = getCurrentLoggedInUser();
        user.setActive(false);
        userRepository.save(user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account deactivated successfully")
                .build();

    }
}
