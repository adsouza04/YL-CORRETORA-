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

    @GetMapping("/planos-residenciais")
    public String planoResidencial() {
        return "plano-residencial";
    }

    @GetMapping("/planos-vida")
    public String planoVida() {
        return "plano-vida";
    }
}