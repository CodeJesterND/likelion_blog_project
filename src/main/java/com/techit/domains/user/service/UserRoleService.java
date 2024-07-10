package com.techit.domains.user.service;

import com.techit.domains.user.entity.UserRole;

import java.util.List;

public interface UserRoleService {
    void registerUserRole(UserRole userRole);
    List<UserRole> getUserRoles();
}