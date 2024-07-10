package com.techit.domains.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class LoginDto {

    private String username;; // 로그인 ID
    private String password; // 로그인 비밀번호
}