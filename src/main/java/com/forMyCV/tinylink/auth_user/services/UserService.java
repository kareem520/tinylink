package com.forMyCV.tinylink.auth_user.services;

import com.forMyCV.tinylink.auth_user.dtos.AccountUpdateRequest;
import com.forMyCV.tinylink.auth_user.dtos.UserDto;
import com.forMyCV.tinylink.auth_user.entity.User;
import com.forMyCV.tinylink.response.Response;

public interface UserService {

    User getCurrentLoggedInUser();
    Response<UserDto> getOwnAccountDetails();

    Response<?> updateOwnAccount(AccountUpdateRequest accountUpdateRequest);

    Response<?> deactivateAccount();

}
