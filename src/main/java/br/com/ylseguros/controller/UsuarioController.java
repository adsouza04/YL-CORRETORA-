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

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.awt.Color;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/minha-conta/baixar-apolice")
    public void baixarApolice(HttpSession session, HttpServletResponse response) throws IOException, DocumentException {
        String email = (String) session.getAttribute("usuarioLogado");
        String nomeReal = (String) session.getAttribute("nomeUsuario");

        if (email == null) {
            response.sendRedirect("/login");
            return;
        }

        List<Apolice> apolices = apoliceRepository.findByUsuarioEmail(email);
        Apolice apolice = apolices.isEmpty() ? null : apolices.get(0);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=apolice.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontTitulo = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(13, 71, 161));
        Font fontSecao  = new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK);
        Font fontNormal = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.DARK_GRAY);
        Font fontBold   = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
        Font fontVerde  = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(21, 128, 61));
        Font fontRodape = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);

        Paragraph titulo = new Paragraph("YL Seguros", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Apólice de Seguro", new Font(Font.HELVETICA, 13, Font.NORMAL, Color.GRAY));
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("____________________________________________________________"));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Dados do Segurado", fontSecao));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nome: " + (nomeReal != null ? nomeReal : "-"), fontNormal));
        document.add(new Paragraph("E-mail: " + email, fontNormal));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Detalhes da Apólice", fontSecao));
        document.add(new Paragraph(" "));

        if (apolice != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String vigencia = apolice.getDataInicio().format(fmt) + " a " + apolice.getDataFim().format(fmt);

            document.add(new Paragraph("Produto: " + apolice.getNomeProduto(), fontBold));
            document.add(new Paragraph("Vigência: " + vigencia, fontNormal));
            document.add(new Paragraph("Valor mensal: R$ " + String.format("%.2f", apolice.getValor()), fontNormal));
            document.add(new Paragraph("Status: ATIVO", fontVerde));
        } else {
            document.add(new Paragraph("Nenhuma apólice ativa encontrada.", fontNormal));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("____________________________________________________________"));
        document.add(new Paragraph(" "));

        Paragraph rodape = new Paragraph("Documento gerado automaticamente | YL Seguros © 2026", fontRodape);
        rodape.setAlignment(Element.ALIGN_CENTER);
        document.add(rodape);

        document.close();
    }
}