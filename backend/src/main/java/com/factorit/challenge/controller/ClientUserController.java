package com.factorit.challenge.controller;

import com.factorit.challenge.model.payload.ClientUserResponse;
import com.factorit.challenge.service.ClientUserService;
import com.factorit.challenge.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
@Tag(name = "Controlador de Clientes", description = "Permite obtener los clientes existentes en base de datos")
public class ClientUserController {
    private final ClientUserService userService;
    private final ShoppingCartService cartService;

    @Operation(summary = "Retorna una lista con todos los clientes disponibles")
    @GetMapping
    public ResponseEntity<List<ClientUserResponse>> getAll(@RequestParam(required = false) Boolean vip,
                                                           @RequestParam(required = false, name = "vip-since") YearMonth vipSince){
        return ResponseEntity.ok(userService.getAll(vip, vipSince));
    }
    @Operation(summary = "Retorna un unico cliente en base al id provisto")
    @GetMapping("/{id}")
    public ResponseEntity<ClientUserResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Retorna los gastos totales del mes corriente de un cliente en base a su id")
    @GetMapping("/{id}/current-month-payments")
    public ResponseEntity<BigDecimal> getClientCurrentMonthPayments(@PathVariable Long id){
        return ResponseEntity.ok(cartService.calculateClientCurrentMonthPayments(id));
    }
}
