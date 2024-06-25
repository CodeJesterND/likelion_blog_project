package com.techit.domains.user.service.impl;


import com.techit.domains.user.dto.UserRegisterDto;
import com.techit.domains.user.entity.Role;
import com.techit.domains.user.entity.User;
import com.techit.domains.user.entity.UserRole;
import com.techit.domains.user.repository.UserRepository;
import com.techit.domains.user.service.RoleService;
import com.techit.domains.user.service.UserRoleService;
import com.techit.domains.user.service.UserService;
import com.techit.standard.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSerivceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long registerUser(UserRegisterDto userRegisterDto) {
        User user = User.builder()
                .username(userRegisterDto.getUsername())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .name(userRegisterDto.getName())
                .email(userRegisterDto.getEmail())
                .nickname(userRegisterDto.getNickname())
                .createdAt(LocalDateTime.now())
                .build();

        Long id = userRepository.save(user).getUserId();
        Role role = roleService.findRoleById(1L);

        UserRole userRole  = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleService.registerUserRole(userRole);

        return id;
    }

    @Override
    public User findUserByUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "에 해당하는 회원을 찾을 수 없습니다."));
    }
}