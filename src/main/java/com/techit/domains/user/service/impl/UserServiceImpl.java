package com.techit.domains.user.service.impl;

import com.techit.domains.user.dto.UserRegisterDto;
import com.techit.domains.user.entity.Role;
import com.techit.domains.user.entity.User;
import com.techit.domains.user.entity.UserRole;
import com.techit.domains.user.repository.UserRepository;
import com.techit.domains.user.service.RoleService;
import com.techit.domains.user.service.UserRoleService;
import com.techit.domains.user.service.UserService;
import com.techit.global.exception.RegistrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleService userRoleService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegisterDto dto) {
        // 중복 확인
        List<String> errors = new ArrayList<>();

        if (isUsernameAvailable(dto.getUsername())) {
            errors.add("이미 사용 중인 아이디입니다.");
        }

        if (isEmailAvailable(dto.getEmail())) {
            errors.add("이미 사용 중인 이메일입니다.");
        }

        if (!errors.isEmpty()) {
            throw new RegistrationException(errors);
        }

        // 사용자 빌드 및 저장
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .createdAt(LocalDateTime.now())
                .isDeactivated(false)
                .build();

        // 사용자 저장
        User registeredUser = userRepository.save(user);

        // 기본 역할 설정
        Role role = roleService.findRoleById(1L);

        // 사용자 역할 빌드 및 저장
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleService.registerUserRole(userRole);

        // 사용자에 역할 설정
        user.setUserRoles(List.of(userRole));

        return registeredUser;
    }

    // 사용자 ID로 사용자 찾기 (읽기 전용 트랜잭션) <- 포스트 때 사용 예정
    @Transactional(readOnly = true)
    @Override
    public User findUserByUserId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 사용자 이름으로 사용자 찾기 (읽기 전용 트랜잭션)
    @Transactional(readOnly = true)
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 아이디가 사용 가능한지 확인 (읽기 전용 트랜잭션)
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return userRepository.existsByUsername(username);
    }

    // 이메일이 사용 가능한지 확인 (읽기 전용 트랜잭션)
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return userRepository.existsByEmail(email);
    }
}