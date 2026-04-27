package br.com.ylseguros.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ylseguros.model.ItemCarrinho;
import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.model.Apolice;
import br.com.ylseguros.repository.ApoliceRepository;
import br.com.ylseguros.repository.ItemCarrinhoRepository;
import br.com.ylseguros.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ApoliceRepository apoliceRepository;

    @GetMapping("/cadastro")
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
            return "redirect:/cadastro";
        }
    }

    @GetMapping("/minha-conta")
    public String exibirMinhaConta(HttpSession session, Model model) {
        String email = (String) session.getAttribute("usuarioLogado");
        String nomeReal = (String) session.getAttribute("nomeUsuario");

        if (email == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioNome", nomeReal);
        model.addAttribute("apolices", apoliceRepository.findByUsuarioEmail(email));

        return "minha-conta";
    }

    @PostMapping("/adicionar-item")
    public String adicionarItem(@RequestParam String nome, @RequestParam Double preco) {
        ItemCarrinho novoItem = new ItemCarrinho(nome, preco);
        itemCarrinhoRepository.save(novoItem);
        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho/dados-pessoais")
    public String formularioCotacao() {
        return "formulario-cotacao";
    }

    @GetMapping("/meus-dados")
    public String exibirDadosPessoais(HttpSession session, Model model) {
        String email = (String) session.getAttribute("usuarioLogado");

        if (email == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        List<Apolice> apolices = apoliceRepository.findByUsuarioEmail(email);

        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }

        if (!apolices.isEmpty()) {
            model.addAttribute("dados", apolices.get(0));
        } else {
            model.addAttribute("dados", new Apolice());
        }

        return "dados-pessoais";
    }

    @PostMapping("/meus-dados/atualizar")
    public String atualizarDados(@RequestParam String email,
            @RequestParam String nome,
            @RequestParam String telefone,
            HttpSession session,
            RedirectAttributes attr) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (usuario != null) {
                usuario.setNome(nome);
                usuario.setTelefone(telefone);

                usuarioService.salvarUsuario(usuario);

                session.setAttribute("nomeUsuario", nome);
                attr.addFlashAttribute("mensagem", "Dados atualizados com sucesso!");
            }

            return "redirect:/meus-dados";

        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao atualizar: " + e.getMessage());
            return "redirect:/meus-dados";
        }
    }
}