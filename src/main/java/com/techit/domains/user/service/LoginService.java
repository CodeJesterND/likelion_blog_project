package com.techit.domains.user.service;

import com.techit.domains.user.entity.User;

import java.util.List;

public interface LoginService {
    public List<String> login(User user);
}
