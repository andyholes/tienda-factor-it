package com.factorit.challenge.controller;

import com.factorit.challenge.model.payload.ShoppingCartRequest;
import com.factorit.challenge.model.payload.ShoppingCartResponse;
import com.factorit.challenge.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.factorit.challenge.utils.MessageConstants.CART_DELETED_SUCCESSFULLY;
import static com.factorit.challenge.utils.MessageConstants.CART_PAID_SUCCESSFULLY;
import static com.factorit.challenge.utils.MessageConstants.PRODUCT_REMOVED_SUCCESSFULLY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping-carts")
@Tag(name = "Controlador de Carritos de Compras", description = "Permite realizar todo tipo de operaciones relacionadas a los carritos de compras. Controlador principal de la aplicacion")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Retorna una lista con todos los carritos de compra. Pueden filtrarse por usuario y por pago/impago")
    @GetMapping
    public ResponseEntity<List<ShoppingCartResponse>> getAll(@RequestParam(required = false, name = "user-id") Long userId,
                                                             @RequestParam(required = false) Boolean paid) {
        return ResponseEntity.ok(shoppingCartService.getAll(userId, paid));
    }

    @Operation(summary = "Retorna un carrito de compra especifico en funcion de un id provisto")
    @GetMapping("/{cart-id}")
    public ResponseEntity<ShoppingCartResponse> getById(@PathVariable(name = "cart-id") Long cartId) {
        return ResponseEntity.ok(shoppingCartService.getById(cartId));
    }

    @Operation(summary = "Permite agregar productos al carrito de compras de un usuario o modificar la cantidad de un mismo producto. Si el carrito no existe primero lo crea")
    @PostMapping
    public ResponseEntity<ShoppingCartResponse> addProduct(@RequestBody ShoppingCartRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(shoppingCartService.addProduct(request));
    }

    @Operation(summary = "Permite quitar productos del carrito de compras de un usuario en base a su id y el id del producto. Si el carrito queda vacio lo elimina")
    @DeleteMapping("/{user-id}/{product-id}")
    public ResponseEntity<String> removeProduct(@PathVariable(name = "user-id") Long userId,
                                                @PathVariable(name = "product-id") Long productId) {
        shoppingCartService.removeProduct(userId, productId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(PRODUCT_REMOVED_SUCCESSFULLY);
    }

    @Operation(summary = "Realiza el pago de un carrito de compras determinado. En funcion de los montos tambien actualiza el estado del usuario (Si califica para ser vip)")
    @PutMapping("/{cart-id}")
    public ResponseEntity<String> pay(@PathVariable(name = "cart-id") Long cartId) {
        shoppingCartService.pay(cartId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(CART_PAID_SUCCESSFULLY);
    }
    @Operation(summary = "Elimina un carrito de compras en funcion de su id")
    @DeleteMapping("/{cart-id}")
    public ResponseEntity<String> delete(@PathVariable(name = "cart-id") Long cartId) {
        shoppingCartService.delete(cartId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CART_DELETED_SUCCESSFULLY);
    }
}
