package com.factorit.challenge.repository;

import com.factorit.challenge.model.ShoppingCart;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @EntityGraph(attributePaths = {"productInShoppingCarts", "user", "productInShoppingCarts.product"})
    List<ShoppingCart> findAll(Specification<ShoppingCart> specification);
}
