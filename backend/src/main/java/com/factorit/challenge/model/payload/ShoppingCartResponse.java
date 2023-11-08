package com.factorit.challenge.model.payload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ShoppingCartResponse(
        Long id,
        List<ProductAndQuantity> products,
        BigDecimal totalPrice,
        BigDecimal priceWithoutDiscounts,
        boolean paid,
        LocalDate creationDate,
        boolean createdOnSpecialDate,
        boolean paidByVipUser
        ) {}
