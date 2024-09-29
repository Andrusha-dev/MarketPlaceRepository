package org.stus.marketplace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LoginController {
    @GetMapping("/loginPage")
    public String getLoginPage() {
        return "loginPage";
    }
}