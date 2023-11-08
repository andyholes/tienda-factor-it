package com.factorit.challenge.model.payload;

import java.time.LocalDate;

public record ShoppingCartRequest(
        Long userId,
        LocalDate creationDate,
        Long productId,
        int quantity
) {
}
