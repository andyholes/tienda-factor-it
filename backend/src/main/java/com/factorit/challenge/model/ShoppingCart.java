package com.factorit.challenge.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ClientUser user;
    private boolean paid;
    private BigDecimal totalPrice;
    private LocalDate creationDate;
    private boolean createdOnSpecialDate;

    private boolean paidByVipUser;
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    Set<ProductInShoppingCart> productInShoppingCarts = new HashSet<>();
}
