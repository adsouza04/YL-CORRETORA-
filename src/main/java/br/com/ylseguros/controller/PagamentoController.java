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
        double totalCalculado = itens.stream().mapToDouble(ItemCarrinho::getPreco).sum();

        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("email", email);
        model.addAttribute("cep", cep);
        model.addAttribute("endereco", (rua != null ? rua : "") + ", " + (numero != null ? numero : ""));
        model.addAttribute("placa", placa);
        model.addAttribute("itens", itens);
        model.addAttribute("total", totalCalculado);

        return "pagamento";
    }

    @PostMapping("/pagamento/confirmar")
    public String confirmarPagamento(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) Double total,
            HttpSession session,
            Model model) {

        // 1. PRIMEIRO: Buscar os itens do banco de dados
        List<ItemCarrinho> itens = repository.findAll();
        String nomeProdutoExibicao = "Seguro Selecionado";
        String placaExtraida = placa;
        Double valorFinal = total;

        // 2. SEGUNDO: Calcular o valor ANTES de apagar os itens
        if (!itens.isEmpty()) {
            ItemCarrinho primeiroItem = itens.get(0);
            String nomeCompleto = primeiroItem.getNomePlano();

            if (nomeCompleto != null && nomeCompleto.contains(" (Placa:")) {
                nomeProdutoExibicao = nomeCompleto.split(" \\(Placa:")[0];
                if (placaExtraida == null || placaExtraida.isEmpty()) {
                    placaExtraida = nomeCompleto.substring(nomeCompleto.indexOf("Placa: ") + 7,
                            nomeCompleto.indexOf(")"));
                }
            } else {
                nomeProdutoExibicao = nomeCompleto;
            }

            // Se o 'total' vindo do formulário for nulo ou zero, calcula pela soma do
            // carrinho
            if (valorFinal == null || valorFinal == 0) {
                valorFinal = itens.stream().mapToDouble(ItemCarrinho::getPreco).sum();
            }
        }

        // 3. TERCEIRO: Processar a gravação da apólice
        String emailLogado = (String) session.getAttribute("usuarioLogado");
        String nomeUsuarioSessao = (String) session.getAttribute("nomeUsuario");

        if (emailLogado != null && valorFinal != null && valorFinal > 0) {
            Apolice novaApolice = new Apolice();
            novaApolice.setUsuarioEmail(emailLogado);
            novaApolice.setNomeProduto(nomeProdutoExibicao);
            novaApolice.setPlaca((placaExtraida == null || placaExtraida.isEmpty()) ? "Não aplicável" : placaExtraida);
            novaApolice.setValor(valorFinal);
            apoliceRepository.save(novaApolice);
        }

        // 4. QUARTO: Preparar a String formatada para o HTML
        String valorExibicao = (valorFinal != null) ? String.format("%.2f", valorFinal).replace(".", ",") : "0,00";

        // 5. QUINTO: Só agora podemos apagar os itens do carrinho com segurança
        repository.deleteAll();

        // Enviar para o Model
        model.addAttribute("nomeCliente", (nome != null && !nome.isEmpty()) ? nome : nomeUsuarioSessao);
        model.addAttribute("nomeProduto", nomeProdutoExibicao);
        model.addAttribute("placaVeiculo",
                (placaExtraida == null || placaExtraida.isEmpty()) ? "Não aplicável" : placaExtraida);
        model.addAttribute("valorPago", valorExibicao);

        return "sucesso";
    }
}