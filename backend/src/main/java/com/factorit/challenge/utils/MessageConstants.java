package com.factorit.challenge.utils;

public class MessageConstants {
    public static final String UTILITY_CLASS_EXCEPTION = "This is a utility class and cannot be instantiated";
    public static final String PRODUCT_NOT_FOUND = "Product was not found";
    public static final String USER_NOT_FOUND = "User was not found";
    public static final String CART_NOT_FOUND = "Shopping cart was not found";
    public static final String QUANTITY_GREATER_THAN_ZERO = "Debe ingresar una candidad mayor a 0";
    public static final String PRODUCT_SAME_QUANTITY = "El producto ya se encuentra en el carrito!";
    public static final String CART_ALREADY_PAYED = "Ya has pagado este carrito!";
    public static final String PRODUCT_REMOVED_SUCCESSFULLY = "El producto se ha removido correctamente!";

    public static final String CART_CANNOT_BE_DELETED = "An already paid shopping cart cannot be deleted";
    public static final String CART_DELETED = "Unpaid cart was deleted successfully";
    public static final String CART_PAYED = "Cart payment was done correctly";
    public static final String CART_NEW = "New unpaid shopping cart created";
    public static final String PRODUCT_NOT_IN_CART = "Shopping cart does not contain the given product";
    public static final String PRODUCT_REMOVED = "A product was removed from shopping cart";
    public static final String DATE_NOT_NULL = "Debe seleccionar una fecha!";
    public static final String USER_NOT_NULL = "Debe identificarse como cliente!";
    public static final String CART_IS_EMPTY = "Debe ingresar productos al carrito!";
    public static final String CART_PAID_SUCCESSFULLY = "El pago del carrito se ha realizado correctamente!";
    public static final String CART_DELETED_SUCCESSFULLY = "Su carrito ha sido borrado!";


    private MessageConstants() {
        throw new UnsupportedOperationException(UTILITY_CLASS_EXCEPTION);
    }
}
