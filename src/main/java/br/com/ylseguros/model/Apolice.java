package br.com.ylseguros.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "apolices")
@Data
public class Apolice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuarioEmail;
    private String nomeProduto;
    private String placa;
    private Double valor;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public Apolice() {
    }

    public Apolice(String usuarioEmail, String nomeProduto, String placa, Double valor, LocalDate dataInicio, LocalDate dataFim) {
        this.usuarioEmail = usuarioEmail;
        this.nomeProduto = nomeProduto;
        this.placa = placa;
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }
}