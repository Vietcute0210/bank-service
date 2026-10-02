package com.vietphan.bank_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/home")
    public String homePage() {
        return "user/home";
    }

    @GetMapping("/account")
    public String accountPage() {
        return "user/account";
    }

    @GetMapping("/create-card")
    public String createCardPage() {
        return "user/create-card";
    }

    @GetMapping("/transfer")
    public String transferPage() {
        return "user/transfer";
    }

    @GetMapping("/history")
    public String historyPage() {
        return "user/history";
    }
}
