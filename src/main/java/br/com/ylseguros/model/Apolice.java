package br.com.ylseguros.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "apolices")
@Data // Gera automaticamente Getters, Setters, toString, etc. (requer o Lombok)
public class Apolice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuarioEmail;

    private String nomeProduto;

    private String placa;

    private Double valor;

    public Apolice() {
    }

    public Apolice(String usuarioEmail, String nomeProduto, String placa, Double valor) {
        this.usuarioEmail = usuarioEmail;
        this.nomeProduto = nomeProduto;
        this.placa = placa;
        this.valor = valor;
    }
}