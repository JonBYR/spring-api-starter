package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.AddToCartDto;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.codewithmosh.store.repositories.CartRepository;

import java.util.Map;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    public CartDto createNewCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }
    public CartItemDto addItemtoCart(AddToCartDto dto, Long id) {
        var product = productRepository.findById(dto.getProductId()).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        var cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var existingProduct = cart.returnCartItem(product.getId());
        if (existingProduct != null) {
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
        }
        else {
            existingProduct = new CartItem();
            existingProduct.setProduct(product);
            existingProduct.setCart(cart);
            existingProduct.setQuantity(1);
            cart.getCartItems().add(existingProduct);
        }
        cartRepository.save(cart);
        var cartItemDto = cartMapper.toDto(existingProduct);
        return cartItemDto;
    }
    public CartDto getCart(Long cartId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var cartDto = cartMapper.toDto(cart);
        return cartDto;
    }
    @Transactional
    public CartItemDto updateQuantity(Long cartId, Long productId, UpdateCartDto dto) {
        var cart =  cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new  CartNotFoundException();
        }
        var cartItem = cart.returnCartItem(productId);
        if (cartItem == null) {
            throw new ProductNotFoundException();
        }
        cartMapper.update(dto, cartItem);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }
    @Transactional
    public void removeCartItem(Long cartId, Long productId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new  CartNotFoundException();
        }
        var cartItem = cart.returnCartItem(productId);
        if (cartItem == null) {
            throw new ProductNotFoundException();
        }
        cart.removeCartItem(productId, cartItem);
        cartRepository.save(cart);
    }
    public void clearList(Long cartId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new  CartNotFoundException();
        }
        else {
            cart.clear();
            cartRepository.save(cart);
        }
    }
}
