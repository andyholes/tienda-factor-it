package com.factorit.challenge.controller;

import com.factorit.challenge.service.SpecialDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dates")
@Tag(name = "Controlador de Fechas Especiales", description = "Permite obtener las fechas especiales persistidas en base de datos")
public class SpecialDateController {
    private final SpecialDateService dateService;

    @Operation(summary = "Retorna todas las fechas especiales en las cuales los clientes pueden acceder a descuentos extra en sus compras")
    @GetMapping
    public ResponseEntity<List<LocalDate>> getAll(){
        return ResponseEntity.ok(dateService.getAll());
    }
}
