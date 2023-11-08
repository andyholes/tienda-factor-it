package com.factorit.challenge.service;

import com.factorit.challenge.model.payload.ShoppingCartRequest;
import com.factorit.challenge.model.payload.ShoppingCartResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCartService {
    ShoppingCartResponse addProduct(ShoppingCartRequest product);

    List<ShoppingCartResponse> getAll(Long userId, Boolean paid);

    ShoppingCartResponse getById(Long id);

    void delete(Long cartId);

    void removeProduct(Long userId, Long productId);

    void pay(Long cartId);

    BigDecimal calculateClientCurrentMonthPayments(Long userId);
}
