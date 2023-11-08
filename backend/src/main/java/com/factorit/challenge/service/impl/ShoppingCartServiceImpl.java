package com.factorit.challenge.service.impl;

import com.factorit.challenge.model.ClientUser;
import com.factorit.challenge.model.Product;
import com.factorit.challenge.model.ProductInShoppingCart;
import com.factorit.challenge.model.ShoppingCart;
import com.factorit.challenge.model.SpecialDate;
import com.factorit.challenge.model.payload.ShoppingCartRequest;
import com.factorit.challenge.model.payload.ShoppingCartResponse;
import com.factorit.challenge.repository.ClientUserRepository;
import com.factorit.challenge.repository.ProductInShoppingCartRepository;
import com.factorit.challenge.repository.ProductRepository;
import com.factorit.challenge.repository.ShoppingCartRepository;
import com.factorit.challenge.repository.SpecialDateRepository;
import com.factorit.challenge.service.ShoppingCartService;
import com.factorit.challenge.utils.EntityMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.factorit.challenge.utils.MessageConstants.CART_ALREADY_PAYED;
import static com.factorit.challenge.utils.MessageConstants.CART_CANNOT_BE_DELETED;
import static com.factorit.challenge.utils.MessageConstants.CART_DELETED;
import static com.factorit.challenge.utils.MessageConstants.CART_IS_EMPTY;
import static com.factorit.challenge.utils.MessageConstants.CART_NEW;
import static com.factorit.challenge.utils.MessageConstants.CART_NOT_FOUND;
import static com.factorit.challenge.utils.MessageConstants.CART_PAYED;
import static com.factorit.challenge.utils.MessageConstants.DATE_NOT_NULL;
import static com.factorit.challenge.utils.MessageConstants.PRODUCT_NOT_FOUND;
import static com.factorit.challenge.utils.MessageConstants.PRODUCT_NOT_IN_CART;
import static com.factorit.challenge.utils.MessageConstants.PRODUCT_REMOVED;
import static com.factorit.challenge.utils.MessageConstants.PRODUCT_SAME_QUANTITY;
import static com.factorit.challenge.utils.MessageConstants.QUANTITY_GREATER_THAN_ZERO;
import static com.factorit.challenge.utils.MessageConstants.USER_NOT_FOUND;
import static com.factorit.challenge.utils.MessageConstants.USER_NOT_NULL;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductInShoppingCartRepository productInShoppingCartRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;
    private final ClientUserRepository userRepository;
    private final SpecialDateRepository dateRepository;

    @Override
    public ShoppingCartResponse addProduct(ShoppingCartRequest request) {
        validateInputData(request);

        ClientUser user = getUserById(request.userId());
        ShoppingCart unpaidCart = getUnpaidCartFromUser(user)
                .orElseGet(() -> shoppingCartRepository.save(createNewEmptyCart(request, user)));

        Product product = getProductById(request.productId());
        ProductInShoppingCart productInShoppingCart = getProductFromShoppingCart(request, unpaidCart, product);

        unpaidCart.getProductInShoppingCarts().add(productInShoppingCart);
        unpaidCart.setTotalPrice(calculateTotalPrice(unpaidCart, unpaidCart.isCreatedOnSpecialDate(), user.isVip()));
        return mapper.toDto(shoppingCartRepository.save(unpaidCart));
    }

    @Override
    public List<ShoppingCartResponse> getAll(Long userId, Boolean paid) {
        return shoppingCartRepository.findAll(setFilters(userId, paid))
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public ShoppingCartResponse getById(Long id) {
        return mapper.toDto(getCartById(id));
    }

    @Override
    public void delete(Long cartId) {
        ShoppingCart shoppingCart = getCartById(cartId);
        if (shoppingCart.isPaid()) throw new UnsupportedOperationException(CART_CANNOT_BE_DELETED);
        shoppingCartRepository.delete(shoppingCart);
        log.info(CART_DELETED);
    }

    @Override
    public void removeProduct(Long userId, Long productId) {
        ShoppingCart shoppingCart = getUnpaidCartFromUser(getUserById(userId))
                .orElseThrow(() -> new EntityNotFoundException(CART_NOT_FOUND));

        ProductInShoppingCart product = shoppingCart.getProductInShoppingCarts()
                .stream().filter(p -> p.getProduct().getId().equals(productId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_IN_CART));

        updateShoppingCart(userId, shoppingCart, product);
        log.info(PRODUCT_REMOVED);
    }

    @Override
    public void pay(Long cartId) {
        ShoppingCart shoppingCart = getCartById(cartId);
        if (shoppingCart.isPaid()) throw new UnsupportedOperationException(CART_ALREADY_PAYED);
        if (shoppingCart.getProductInShoppingCarts().isEmpty()) throw new UnsupportedOperationException(CART_IS_EMPTY);
        shoppingCart.setPaid(true);
        shoppingCart.setPaidByVipUser(shoppingCart.getUser().isVip());
        checkIfUserBecomesVip(shoppingCart.getUser());
        shoppingCartRepository.save(shoppingCart);
        log.info(CART_PAYED);
    }

    @Override
    public BigDecimal calculateClientCurrentMonthPayments(Long userId) {
        return getUserById(userId).getShoppingCarts().stream()
                .filter(cart -> cart.getCreationDate().getMonth().equals(LocalDate.now().getMonth()))
                .map(cart -> calculateTotalPrice(cart,cart.isCreatedOnSpecialDate(),cart.isPaidByVipUser())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ProductInShoppingCart setProductQuantity(int quantity, ProductInShoppingCart productInCart) {
        if (productInCart.getQuantity() == quantity)
            throw new IllegalArgumentException(PRODUCT_SAME_QUANTITY);
        productInCart.setQuantity(quantity);
        return productInCart;
    }

    private ProductInShoppingCart getProductFromShoppingCart(ShoppingCartRequest request, ShoppingCart unpaidCart, Product product) {
        return unpaidCart.getProductInShoppingCarts()
                .stream().filter(productInShoppingCart -> productInShoppingCart.getProduct().equals(product)).findFirst()
                .map(productInShoppingCart -> setProductQuantity(request.quantity(), productInShoppingCart))
                .orElseGet(() -> addProductToCart(request, product, unpaidCart));
    }

    private ProductInShoppingCart addProductToCart(ShoppingCartRequest request, Product product, ShoppingCart cart) {
        return productInShoppingCartRepository.save(
                ProductInShoppingCart.builder()
                        .shoppingCart(cart)
                        .product(product)
                        .quantity(request.quantity())
                        .build());
    }

    private void validateInputData(ShoppingCartRequest request) {
        if (request.quantity() <= 0) throw new IllegalArgumentException(QUANTITY_GREATER_THAN_ZERO);
        if (request.userId() == null) throw new IllegalArgumentException(USER_NOT_NULL);
        if (request.creationDate() == null) throw new IllegalArgumentException(DATE_NOT_NULL);
    }

    private ShoppingCart createNewEmptyCart(ShoppingCartRequest request, ClientUser user) {
        log.info(CART_NEW);
        return ShoppingCart.builder()
                .creationDate(request.creationDate())
                .paid(false)
                .totalPrice(BigDecimal.ZERO)
                .productInShoppingCarts(new HashSet<>())
                .user(user)
                .createdOnSpecialDate(dateRepository.findAll().stream().map(this::monthDayToLocalDate)
                        .toList().contains(request.creationDate()))
                .build();
    }

    private LocalDate monthDayToLocalDate(SpecialDate date) {
        int currentYear = LocalDate.now().getYear();
        return LocalDate.of(currentYear, date.getDate().getMonth(), date.getDate().getDayOfMonth());
    }

    private ClientUser getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    private Optional<ShoppingCart> getUnpaidCartFromUser(ClientUser user) {
        return user.getShoppingCarts().stream().filter(cart -> !cart.isPaid()).findFirst();
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND));
    }

    private Specification<ShoppingCart> setFilters(Long userId, Boolean paid) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(userId).ifPresent(id -> {
                Path<ClientUser> user = root.get("user");
                predicates.add(criteriaBuilder.equal(user.get("id"), id));
            });
            Optional.ofNullable(paid).ifPresent(
                    paidValue -> predicates.add(criteriaBuilder.equal(root.get("paid"), paidValue))
            );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ShoppingCart getCartById(Long id) {
        return shoppingCartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CART_NOT_FOUND));
    }

    private BigDecimal calculateTotalPrice(ShoppingCart cart, boolean createdOnSpecialDate, boolean vip) {
        BigDecimal totalPrice = calculateShoppingCartPrice(cart);
        Set<ProductInShoppingCart> products = cart.getProductInShoppingCarts();
        int productsQuantity = products.stream().mapToInt(ProductInShoppingCart::getQuantity).sum();
        if (productsQuantity == 4) {
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(0.75));
        }
        if (productsQuantity >= 10) {
            if (vip) {
                totalPrice = applyVipDiscount(totalPrice, products);
            }
            if (createdOnSpecialDate && !vip) {
                totalPrice = totalPrice.subtract(BigDecimal.valueOf(300));
            } else {
                totalPrice = totalPrice.subtract(BigDecimal.valueOf(100));
            }
        }
        return totalPrice;
    }

    private BigDecimal applyVipDiscount(BigDecimal totalPrice, Set<ProductInShoppingCart> products) {
        return totalPrice.subtract(BigDecimal.valueOf(500))
                .subtract(products.stream().map(product -> product.getProduct().getPrice())
                        .min(Comparator.naturalOrder())
                        .orElse(totalPrice)
                );
    }

    private BigDecimal calculateShoppingCartPrice(ShoppingCart cart) {
        Set<ProductInShoppingCart> products = cart.getProductInShoppingCarts();
        BigDecimal totalPrice = BigDecimal.ZERO;
        return products.stream().map(product ->
                        totalPrice.add(product.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(product.getQuantity()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateShoppingCart(Long userId, ShoppingCart shoppingCart, ProductInShoppingCart product) {
        shoppingCart.getProductInShoppingCarts().remove(product);
        shoppingCart.setTotalPrice(calculateTotalPrice(shoppingCart, shoppingCart.isCreatedOnSpecialDate(), getUserById(userId).isVip()));
        shoppingCartRepository.save(shoppingCart);
        productInShoppingCartRepository.deleteById(product.getId());
        if (shoppingCart.getProductInShoppingCarts().isEmpty()) {
            delete(shoppingCart.getId());
        }
    }

    private void checkIfUserBecomesVip(ClientUser user) {
        boolean userHasPayedEnough = BigDecimal.valueOf(10000).compareTo(calculateClientCurrentMonthPayments(user.getId())) < 0;
        if (!user.isVip() && userHasPayedEnough) {
            userRepository.findById(user.getId()).ifPresent(updatedUser -> {
                updatedUser.setVip(true);
                updatedUser.setVipUpdateDate(YearMonth.now());
                userRepository.save(updatedUser);
                String fullName = user.getName() + " " + user.getLastName();
                log.info(fullName + " is now a vip client!");
            });
        }
    }

    //Aca deberia usar @Scheduled(cron="@daily") para ejecutarlo al comienzo de cada dia
    //pero lo configure cada 5 minutos asi se puede ver como funciona
    @Scheduled(cron = "0 */5 * * * *")
    private void removeUnpaidCartsDaily() {
        log.info("Removing unpaid carts from users");
        userRepository.findAll().forEach(user ->
                getUnpaidCartFromUser(user)
                        .ifPresent(cart -> delete(cart.getId())));
    }
}
