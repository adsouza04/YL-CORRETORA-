package br.com.ylseguros.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FluxoController {

    
    @GetMapping("/finalizar")
    public String finalizar(HttpSession session) {

        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/pagamento";
        }

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/home";
    }
}