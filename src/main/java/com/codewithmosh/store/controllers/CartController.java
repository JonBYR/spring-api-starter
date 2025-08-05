package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddToCartDto;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartItemRepository;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import com.codewithmosh.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> addCart(UriComponentsBuilder uriBuilder) {
        var cartDto = cartService.createNewCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addProduct(@Valid @RequestBody AddToCartDto dto, @PathVariable(name = "cartId") Long id) {

        var cartItemDto = cartService.addItemtoCart(dto, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCart(@PathVariable(name = "id") Long id) {
        var cartDto = cartService.getCart(id);
        return ResponseEntity.ok(cartDto);
    }
    @PutMapping("/{cartID}/items/{productID}")
    public ResponseEntity<CartItemDto> updateQuantity(
            @PathVariable(name = "cartID") Long cartId,
            @PathVariable(name = "productID") Long productId,
            @Valid @RequestBody UpdateCartDto dto
    ) {
        var cartItemDto = cartService.updateQuantity(cartId, productId, dto);
        return ResponseEntity.ok(cartItemDto);
    }
    @DeleteMapping("/{cartID}/items/{productID}")
    public ResponseEntity<?> deleteItem(
            @PathVariable(name = "cartID") Long cartId,
            @PathVariable(name = "productID") Long productId
    )
    {
        cartService.removeCartItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{cartID}/items")
    public ResponseEntity<?> clearCart(@PathVariable(name = "cartID") Long cartId) {
        cartService.clearList(cartId);
        return ResponseEntity.noContent().build();
    }
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart was not found"));
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Product was not found in cart"));
    }
}

