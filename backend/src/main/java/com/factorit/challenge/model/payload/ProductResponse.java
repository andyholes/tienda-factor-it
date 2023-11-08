package com.factorit.challenge.model.payload;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price, String image) {
}
