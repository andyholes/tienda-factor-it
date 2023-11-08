package com.factorit.challenge.service;

import com.factorit.challenge.model.payload.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();

    ProductResponse getById(Long id);
}
