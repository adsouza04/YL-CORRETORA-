package br.com.ylseguros.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String perfil;
    private String telefone;
    private String cep;
    private String cpf;
    private String numero;
    private String complemento;
    private String logradouro;
    private String bairro;
    private String cidade;
}