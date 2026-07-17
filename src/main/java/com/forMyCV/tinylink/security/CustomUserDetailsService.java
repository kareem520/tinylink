package com.forMyCV.tinylink.security;

import com.forMyCV.tinylink.auth_user.entity.User;
import com.forMyCV.tinylink.auth_user.repository.UserRepository;
import com.forMyCV.tinylink.exceptions.BusinessException;
import com.forMyCV.tinylink.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_EMAIL_NOT_FOUND));

        return MyUserDetails.builder()
                .user(user)
                .build();
    }
}
