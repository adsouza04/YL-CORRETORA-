package br.com.ylseguros.service;

import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void salvarUsuario(Usuario usuario) {
        // 1. Verifica se o e-mail já está no banco
        Usuario existente = usuarioRepository.findByEmail(usuario.getEmail());

        if (existente != null) {
            // Se encontrou alguém, lança o erro que o Controller vai capturar
            throw new RuntimeException("Este e-mail já está em uso por outro usuário!");
        }

        // 2. Se não existir, salva normalmente
        usuarioRepository.save(usuario);
    }

    public Usuario realizarLogin(String email, String senha) {
        return usuarioRepository.findByEmailAndSenha(email, senha);
    }

}
