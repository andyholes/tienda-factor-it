package com.factorit.challenge.service.impl;

import com.factorit.challenge.model.payload.ProductResponse;
import com.factorit.challenge.repository.ProductRepository;
import com.factorit.challenge.service.ProductService;
import com.factorit.challenge.utils.EntityMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.factorit.challenge.utils.MessageConstants.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final EntityMapper mapper;
    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public ProductResponse getById(Long id){
        return mapper.toDto(productRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(PRODUCT_NOT_FOUND)));
    }
}
