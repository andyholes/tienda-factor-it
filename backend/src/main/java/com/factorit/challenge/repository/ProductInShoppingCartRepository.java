package com.factorit.challenge.repository;

import com.factorit.challenge.model.ProductInShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInShoppingCartRepository extends JpaRepository<ProductInShoppingCart, Long> {
}
