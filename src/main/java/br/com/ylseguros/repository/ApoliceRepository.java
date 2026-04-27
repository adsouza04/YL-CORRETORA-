package br.com.ylseguros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.ylseguros.model.Apolice;
import java.util.List;

@Repository
public interface ApoliceRepository extends JpaRepository<Apolice, Long> {

    List<Apolice> findByUsuarioEmail(String usuarioEmail);
}