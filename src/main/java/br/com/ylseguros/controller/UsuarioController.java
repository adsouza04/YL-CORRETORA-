package br.com.ylseguros.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ylseguros.model.ItemCarrinho;
import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.repository.ItemCarrinhoRepository;
import br.com.ylseguros.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @GetMapping("/cadastro-cliente")
    public String exibirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro-cliente";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(Usuario usuario, RedirectAttributes attr) {
        try {
            usuarioService.salvarUsuario(usuario);
            attr.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
            return "redirect:/login";
        } catch (RuntimeException e) {
            attr.addFlashAttribute("erro", e.getMessage());
            return "redirect:/cadastro-cliente";
        }
    }

    // ÚNICA ROTA DASHBOARD DO PROJETO
    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        return "area-cliente";
    }

    @PostMapping("/efetuarLogin")
    public String login(@RequestParam String email, @RequestParam String senha, Model model, RedirectAttributes attr) {
        Usuario userLogado = usuarioService.realizarLogin(email, senha);

        if (userLogado != null) {
            if ("ADM".equals(userLogado.getPerfil())) {
                return "redirect:/admin/painel";
            }
            model.addAttribute("usuario", userLogado);
            return "area-cliente";
        } else {
            attr.addFlashAttribute("erro", "Email ou senha inválidos!");
            return "redirect:/login";
        }
    }

    @GetMapping("/admin/painel")
    public String exibirPainelAdm() {
        return "painel-controle";
    }

    // Rota para ADICIONAR item ao carrinho (Banco de Dados)
    @PostMapping("/adicionar-item")
    public String adicionarItem(@RequestParam String nome, @RequestParam Double preco) {
        ItemCarrinho novoItem = new ItemCarrinho(nome, preco);
        itemCarrinhoRepository.save(novoItem);
        return "redirect:/carrinho";
    }

    // Rota para EXIBIR a lista do carrinho
    @GetMapping("/carrinho")
    public String mostrarCarrinho(Model model) {
        List<ItemCarrinho> lista = itemCarrinhoRepository.findAll();
        model.addAttribute("itens", lista);

        // Soma o total de todos os itens no carrinho
        Double total = lista.stream().mapToDouble(ItemCarrinho::getPreco).sum();
        model.addAttribute("totalGeral", total);

        return "carrinho";
    }
}