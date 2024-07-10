package com.techit.domains.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

    @NotBlank(message = "아이디는 공백을 허용하지 않습니다.")
    @Size(min = 4, max = 30, message = "아이디는 4 ~ 30자이내로 허용합니다.")
    private String  username;

    @NotBlank(message = "비밀번호는 공백을 허용하지 않습니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).*$",
            message = "비밀번호는 특수문자(@, #, $, %, ^, &, +, =, !), 영문자, 숫자를 포함합니다.")
    @Size(min = 8, max = 30, message = "비밀번호는 8자 이상만 허용합니다.")
    private String password;

    @NotBlank(message = "이름은 공백을 허용하지 않습니다.")
    @Pattern(regexp = "^[가-힣]*$", message = "한글만 입력 가능합니다")
    @Size(min= 2, max = 20, message = "이름은 2 ~ 20자이내로 허용합니다.")
    private String name;

    @NotBlank(message = "이메일은 공백을 허용하지 않습니다.")
    @Email(message = "이메일 형식만 입력 가능합니다.")
    @Size(min= 1, max = 50)
    private String email;

    @NotBlank(message = "닉네임은 공백을 허용하지 않습니다.")
    @Size(min= 1, max = 20, message = "닉네임은 1 ~ 20자이내로 허용합니다.")
    private String nickname;
}