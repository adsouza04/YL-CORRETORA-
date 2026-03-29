package br.com.ylseguros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.ylseguros.model.ItemCarrinho;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
}