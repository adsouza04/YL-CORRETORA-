package br.com.ylseguros.controller;

import br.com.ylseguros.model.Seguro;
import br.com.ylseguros.repository.SeguroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private SeguroRepository seguroRepository;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("planos", seguroRepository.findAll());
        return "home";
    }

    @GetMapping("/planos")
    public String planos(Model model) {
        List<Seguro> todosOsSeguros = seguroRepository.findAll();
        List<Seguro> planosAuto = todosOsSeguros.stream()
                .filter(s -> "Auto".equalsIgnoreCase(s.getCategoria()))
                .collect(Collectors.toList());
        model.addAttribute("planosNovos", planosAuto);

        return "planos";
    }

    @GetMapping("/planos-residenciais")
    public String planoResidencial(Model model) {
        model.addAttribute("planos", seguroRepository.findAll()
                .stream()
                .filter(s -> "Residencial".equalsIgnoreCase(s.getCategoria()))
                .toList());
        return "plano-residencial";
    }

    @GetMapping("/planos-vida")
    public String planoVida(Model model) {
        model.addAttribute("planos", seguroRepository.findAll()
                .stream()
                .filter(s -> "Vida".equalsIgnoreCase(s.getCategoria()))
                .toList());
        return "plano-vida";
    }
}