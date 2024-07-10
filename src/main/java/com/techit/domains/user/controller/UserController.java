package com.techit.domains.user.controller;

import com.techit.domains.blog.dto.BlogCreateDto;
import com.techit.domains.blog.service.BlogService;
import com.techit.domains.user.dto.UserRegisterDto;
import com.techit.domains.user.entity.User;
import com.techit.domains.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BlogService blogService;

    @GetMapping("/register")
    public String registerUserForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());

        return "user/user-register-form";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                System.out.println("Field: " + error.getField() + ", Message: " + error.getDefaultMessage());
            });
            return "user/user-register-form";
        }

        try {
            User user = userService.registerUser(userRegisterDto);

            BlogCreateDto blogCreateDto = new BlogCreateDto();
            blogCreateDto.setTitle(user.getNickname()+"'s 블로그");
            blogCreateDto.setUsername(user.getUsername());

            blogService.createBlog(blogCreateDto);

            return "redirect:/login-form";
        }
        catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "user/user-register-form";
        }
    }
}