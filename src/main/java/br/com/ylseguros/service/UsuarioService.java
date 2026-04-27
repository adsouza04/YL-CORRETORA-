package br.com.ylseguros.service;

import br.com.ylseguros.model.Usuario;
import br.com.ylseguros.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void salvarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario realizarLogin(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }
}