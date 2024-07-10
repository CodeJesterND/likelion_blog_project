package com.techit.domains.user.service.impl;

import com.techit.domains.user.entity.UserRole;
import com.techit.domains.user.repository.UserRoleRepository;
import com.techit.domains.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public void registerUserRole(UserRole userRole) { // 유저 권한 저장
        userRoleRepository.save(userRole);
    }

    @Override
    public List<UserRole> getUserRoles() { // 모든 유저 권한 반환
        return userRoleRepository.findAll();
    }
}