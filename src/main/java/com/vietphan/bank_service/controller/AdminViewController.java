package com.vietphan.bank_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String userCreate() {
        return "admin/user-create";
    }

    @GetMapping("/users/{id}")
    public String userDetail() {
        return "admin/user-detail";
    }

    @GetMapping("/users/{id}/update")
    public String userUpdate() {
        return "admin/user-update";
    }

    @GetMapping("/cards")
    public String cards() {
        return "admin/cards";
    }

    @GetMapping("/cards/{id}")
    public String cardDetail() {
        return "admin/card-detail";
    }

    @GetMapping("/transactions")
    public String transactions() {
        return "admin/transactions";
    }

    @GetMapping("/user-levels")
    public String userLevels() {
        return "admin/user-levels";
    }

    @GetMapping("/user-levels/create")
    public String userLevelCreate() {
        return "admin/user-level-create";
    }
}
