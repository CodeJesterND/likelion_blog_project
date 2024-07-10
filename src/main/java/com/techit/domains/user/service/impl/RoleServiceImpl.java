package com.techit.domains.user.service.impl;

import com.techit.domains.user.entity.Role;
import com.techit.domains.user.repository.RoleRepository;
import com.techit.domains.user.service.RoleService;
import com.techit.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id + "에 해당하는 권한을 찾을 수 없습니다."));
    }
}