package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddToCartDto;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartItemRepository;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @PostMapping
    public ResponseEntity<CartDto> addCart(UriComponentsBuilder uriBuilder) {
        var cart = new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddToCartDto dto, @PathVariable(name = "cartId") Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            ResponseEntity.badRequest().build();
        }
        var cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            ResponseEntity.notFound().build();
        }
        //var cartItem;
    }
}
