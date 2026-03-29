package br.com.ylseguros.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomePlano;
    private Double preco;

    // Construtor padrão (obrigatório para o Banco de Dados)
    public ItemCarrinho() {
    }

    // Construtor para facilitar a criação do item
    public ItemCarrinho(String nomePlano, Double preco) {
        this.nomePlano = nomePlano;
        this.preco = preco;
    }

    // Getters e Setters (para o Java conseguir ler e escrever os dados)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomePlano() { return nomePlano; }
    public void setNomePlano(String nomePlano) { this.nomePlano = nomePlano; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
}