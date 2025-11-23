package com.example.board.controller;

import com.example.board.service.MemberService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    public static class RegisterForm {
        @NotBlank @Size(min = 3, max = 30)
        public String username;
        @NotBlank @Size(min = 8, max = 50)
        public String password;
        @Email @NotBlank
        public String email;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Validated RegisterForm form, Model model) {
        try {
            memberService.register(form.username, form.password, form.email);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
