package br.com.ylseguros.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.service.UsuarioService;
import br.com.ylseguros.repository.ItemCarrinhoRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/efetuarLogin")
    public String efetuarLogin(@RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            RedirectAttributes attr) {

        Usuario usuario = usuarioService.realizarLogin(email, senha);

        if (usuario != null) {
            session.setAttribute("usuarioLogado", usuario.getEmail());
            session.setAttribute("nomeUsuario", usuario.getNome());
            session.setAttribute("perfilUsuario", usuario.getPerfil());

            if ("ADM".equals(usuario.getPerfil())) {
                return "redirect:/admin/painel";
            }

            boolean temItensNoCarrinho = itemCarrinhoRepository.count() > 0;

            if (temItensNoCarrinho) {
                return "redirect:/pagamento";
            } else {
                return "redirect:/minha-conta";
            }

        } else {
            attr.addFlashAttribute("erro", "E-mail ou senha incorretos!");
            return "redirect:/login";
        }
    }

    @GetMapping("/sair")
    public String sair(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping("/ajuda")
    public String suporte() {
        return "chat"; 
    }
}