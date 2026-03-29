package br.com.ylseguros.repository;

import br.com.ylseguros.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);

    Usuario findByEmailAndSenha(String email, String senha);

}
