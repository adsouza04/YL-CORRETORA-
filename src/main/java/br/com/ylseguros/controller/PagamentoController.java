package br.com.ylseguros.controller;

import br.com.ylseguros.model.Apolice;
import br.com.ylseguros.model.ItemCarrinho;
import br.com.ylseguros.repository.ApoliceRepository;
import br.com.ylseguros.repository.ItemCarrinhoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PagamentoController {

    @Autowired
    private ItemCarrinhoRepository repository;

    @Autowired
    private ApoliceRepository apoliceRepository;

    @GetMapping("/pagamento")
    public String pagamento(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) String rua,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String placa,
            Model model) {

        List<ItemCarrinho> itens = repository.findAll();
        double total = itens.stream().mapToDouble(ItemCarrinho::getPreco).sum();

        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("email", email);
        model.addAttribute("cep", cep);
        model.addAttribute("endereco", (rua != null ? rua : "") + ", " + (numero != null ? numero : ""));
        model.addAttribute("placa", placa);

        model.addAttribute("itens", itens);
        model.addAttribute("total", total);

        return "pagamento";
    }

    @PostMapping("/pagamento/confirmar")
    public String confirmarPagamento(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) Double total,
            HttpSession session,
            Model model) {

        List<ItemCarrinho> itens = repository.findAll();
        String nomeProdutoExibicao = "Seguro Selecionado";
        String placaExtraida = placa;

        if (!itens.isEmpty()) {
            String nomeCompleto = itens.get(0).getNomePlano();
            if (nomeCompleto != null && nomeCompleto.contains(" (Placa:")) {
                nomeProdutoExibicao = nomeCompleto.split(" \\(Placa:")[0];
                if (placaExtraida == null || placaExtraida.isEmpty()) {
                    placaExtraida = nomeCompleto.substring(nomeCompleto.indexOf("Placa: ") + 7,
                            nomeCompleto.indexOf(")"));
                }
            } else {
                nomeProdutoExibicao = nomeCompleto;
            }

            if (total == null) {
                total = itens.get(0).getPreco();
            }
        }

        String emailLogado = (String) session.getAttribute("usuarioLogado");
        String nomeUsuarioSessao = (String) session.getAttribute("nomeUsuario");

        if (emailLogado != null) {
            Apolice novaApolice = new Apolice();
            novaApolice.setUsuarioEmail(emailLogado);
            novaApolice.setNomeProduto(nomeProdutoExibicao);
            novaApolice.setPlaca((placaExtraida == null || placaExtraida.isEmpty()) ? "Não aplicável" : placaExtraida);
            novaApolice.setValor(total);

            apoliceRepository.save(novaApolice);
        }

        repository.deleteAll();

        model.addAttribute("nomeCliente", (nome != null) ? nome : nomeUsuarioSessao);
        model.addAttribute("nomeProduto", nomeProdutoExibicao);
        model.addAttribute("placaVeiculo",
                (placaExtraida == null || placaExtraida.isEmpty()) ? "Não aplicável" : placaExtraida);
        model.addAttribute("valorPago", total);

        return "sucesso";
    }
}