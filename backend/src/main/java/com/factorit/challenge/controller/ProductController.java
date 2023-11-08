package com.factorit.challenge.controller;

import com.factorit.challenge.model.payload.ProductResponse;
import com.factorit.challenge.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Controlador de Productos", description = "Permite obtener los productos existentes en base de datos")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Retorna una lista con todos los productos disponibles para ser comprados")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @Operation(summary = "Retorna un unico producto en base a la id provista")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getById(id));
    }
}
