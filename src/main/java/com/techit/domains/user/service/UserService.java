package com.techit.domains.user.service;

import com.techit.domains.user.dto.UserRegisterDto;
import com.techit.domains.user.entity.User;

public interface UserService {

    Long registerUser(UserRegisterDto dto);
    User findUserByUserId(Long id);
}