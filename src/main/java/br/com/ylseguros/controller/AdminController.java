package br.com.ylseguros.controller;

import br.com.ylseguros.model.Apolice;
import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.model.Seguro;
import br.com.ylseguros.repository.ApoliceRepository;
import br.com.ylseguros.repository.SeguroRepository;
import br.com.ylseguros.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApoliceRepository apoliceRepository;

    @Autowired
    private SeguroRepository seguroRepository;

    // --- CONTROLE DE ACESSO AO PAINEL ---
    @GetMapping("/admin/painel")
    public String painelAdmin(HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }
        return "painel";
    }

    // --- GESTÃO DE USUÁRIOS E APÓLICES ---
    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        List<Apolice> todasApolices = apoliceRepository.findAll();
        List<Usuario> clientesComSeguro = todasApolices.stream()
                .map(apolice -> usuarioService.listarTodos().stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(apolice.getUsuarioEmail()))
                        .findFirst()
                        .orElse(null))
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

    // --- GESTÃO DE PRODUTOS (PLANOS) ---

    // Lista todos os planos cadastrados no banco
    @GetMapping("/admin/configurar")
    public String gerenciarPlanos(Model model, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        model.addAttribute("planos", seguroRepository.findAll());
        return "gestao-planos";
    }

    // Abre o formulário para NOVO (id=0) ou para EDITAR (id > 0)
    @GetMapping("/admin/configurar/editar/{id}")
    public String editar(@PathVariable Long id, Model model, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        if (id != null && id > 0) {
            // Se o ID existe, carrega do banco. Se não achar, cria um novo objeto para não
            // quebrar a tela.
            model.addAttribute("seguro", seguroRepository.findById(id).orElse(new Seguro()));
        } else {
            // Se o ID for 0, envia um objeto vazio para o formulário de NOVO PRODUTO
            model.addAttribute("seguro", new Seguro());
        }
        return "formulario-plano";
    }

    // Recebe os dados do formulário e salva/atualiza no banco
    @PostMapping("/admin/configurar/salvar")
    public String salvarPlano(@ModelAttribute Seguro seguro, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        seguroRepository.save(seguro);
        return "redirect:/admin/configurar";
    }

    // Remove um plano do banco
    @GetMapping("/admin/configurar/deletar/{id}")
    public String deletarPlano(@PathVariable("id") Long id, HttpSession session) {
        String perfil = (String) session.getAttribute("perfilUsuario");
        if (perfil == null || !perfil.equals("ADM")) {
            return "redirect:/login";
        }

        seguroRepository.deleteById(id);
        return "redirect:/admin/configurar";
    }
}