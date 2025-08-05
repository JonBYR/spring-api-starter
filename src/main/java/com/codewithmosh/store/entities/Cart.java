package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDateTime dateCreated;
    @OneToMany(mappedBy = "cart", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    public BigDecimal getTotalPrice() {
        if (cartItems == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            totalPrice = totalPrice.add(cartItem.getTotalPrice());
        }
        return totalPrice;
    }

    public CartItem returnCartItem(Long productId) {
        return getCartItems().stream().filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
    }
    public void removeCartItem(Long productId, CartItem c) {
        getCartItems().remove(c);
        c.setCart(null);
    }
    public void clear() {
        cartItems.clear();
    }
}
