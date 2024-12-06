package com.devops.devops_user.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/devops-user")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Hello, from user service!!!!!!";
    }
}