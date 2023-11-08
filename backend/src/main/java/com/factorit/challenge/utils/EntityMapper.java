package com.factorit.challenge.utils;

import com.factorit.challenge.model.ClientUser;
import com.factorit.challenge.model.Product;
import com.factorit.challenge.model.ProductInShoppingCart;
import com.factorit.challenge.model.ShoppingCart;
import com.factorit.challenge.model.payload.ClientUserResponse;
import com.factorit.challenge.model.payload.ProductAndQuantity;
import com.factorit.challenge.model.payload.ProductResponse;
import com.factorit.challenge.model.payload.ShoppingCartResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class EntityMapper {
    public ProductResponse toDto(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                Base64.encodeBase64String(product.getImage())
        );
    }

    public ClientUserResponse toDto(ClientUser user){
        return new ClientUserResponse(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.isVip()
        );
    }

    public ShoppingCartResponse toDto(ShoppingCart shoppingCart) {
        List<ProductAndQuantity> products =
                shoppingCart.getProductInShoppingCarts().stream().map(this::toDto).toList();
        BigDecimal priceWithoutDiscounts = products.stream()
                .map(cart -> cart.product().getPrice().multiply(new BigDecimal(cart.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ShoppingCartResponse(
                shoppingCart.getId(),
                products,
                shoppingCart.getTotalPrice(),
                priceWithoutDiscounts,
                shoppingCart.isPaid(),
                shoppingCart.getCreationDate(),
                shoppingCart.isCreatedOnSpecialDate(),
                shoppingCart.isPaidByVipUser()
        );
    }


    private ProductAndQuantity toDto(ProductInShoppingCart cart){
        return new ProductAndQuantity(cart.getProduct(), cart.getQuantity());
    }
}
