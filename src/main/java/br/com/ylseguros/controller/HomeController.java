package br.com.ylseguros.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/planos")
    public String planos() {
        return "planos";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}