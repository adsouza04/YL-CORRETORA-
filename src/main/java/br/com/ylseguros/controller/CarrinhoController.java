package br.com.ylseguros.controller;

import br.com.ylseguros.model.ItemCarrinho;
import br.com.ylseguros.repository.ItemCarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private ItemCarrinhoRepository repository;

    @GetMapping
    public String exibirCarrinho(Model model) {
        List<ItemCarrinho> itens = repository.findAll();
        double total = itens.stream().mapToDouble(ItemCarrinho::getPreco).sum();

        model.addAttribute("itens", itens);
        model.addAttribute("total", total);

        return "carrinho";
    }

    @GetMapping("/identificar")
    public String identificarVeiculo(@RequestParam String nome,
            @RequestParam Double preco,
            Model model) {
        model.addAttribute("nome", nome);
        model.addAttribute("preco", preco);
        return "solicitar-placa";
    }

    @PostMapping("/add")
    public String adicionarAoCarrinho(@RequestParam String nome,
            @RequestParam Double preco,
            @RequestParam(required = false) String placa) {

        String nomeFinal = nome;

        if (placa != null && !placa.isEmpty()) {
            nomeFinal = nome + " (Placa: " + placa.toUpperCase() + ")";
        }

        ItemCarrinho novoItem = new ItemCarrinho(nomeFinal, preco);
        repository.save(novoItem);

        return "redirect:/carrinho";
    }

    @GetMapping("/resumo")
    public String exibirResumo(Model model) {
        List<ItemCarrinho> itens = repository.findAll();
        double total = itens.stream().mapToDouble(ItemCarrinho::getPreco).sum();

        boolean temSeguroAuto = itens.stream()
                .anyMatch(item -> item.getNomePlano().toLowerCase().contains("auto"));

        model.addAttribute("itens", itens);
        model.addAttribute("total", total);
        model.addAttribute("temSeguroAuto", temSeguroAuto);

        return "resumo";
    }

    @GetMapping("/limpar")
    public String limparCarrinho() {
        repository.deleteAll();
        return "redirect:/home";
    }

    @GetMapping("/remover/{id}")
    public String removerDoCarrinho(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/carrinho";
    }
}