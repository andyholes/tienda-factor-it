package com.factorit.challenge.model.payload;

import com.factorit.challenge.model.Product;

public record ProductAndQuantity(Product product, int quantity) {}
