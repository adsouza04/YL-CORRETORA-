package br.com.ylseguros.controller;

import br.com.ylseguros.model.Apolice;
import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.repository.ApoliceRepository;
import br.com.ylseguros.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApoliceRepository apoliceRepository;

    @GetMapping("/admin/painel")
    public String painelAdmin(HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");

        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        return "painel";
    }

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        List<Apolice> todasApolices = apoliceRepository.findAll();

        List<Usuario> clientesComSeguro = todasApolices.stream()
                .map(apolice -> {

                    return usuarioService.listarTodos().stream()
                            .filter(u -> u.getEmail().equalsIgnoreCase(apolice.getUsuarioEmail()))
                            .findFirst()
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("usuarios", clientesComSeguro);

        return "lista-usuario";
    }

    @GetMapping("/admin/usuarios/seguros/{email}")
    public String verSegurosUsuario(@PathVariable String email, Model model, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        List<Apolice> apolices = apoliceRepository.findByUsuarioEmail(email);

        model.addAttribute("cliente", usuario);
        model.addAttribute("apolices", apolices);

        return "detalhes-seguros";
    }
}