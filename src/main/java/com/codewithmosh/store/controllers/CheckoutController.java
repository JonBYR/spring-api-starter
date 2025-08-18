package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutDto;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.CartService;
import com.codewithmosh.store.services.CheckoutService;
import com.codewithmosh.store.services.OrderService;
import com.codewithmosh.store.services.WebhookRequest;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {
    private final CartService cartService;
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody CheckoutDto dto) {
        System.out.println(dto.getId());
        var cartDto = cartService.getCart(dto.getId());
        System.out.println("Function finished");
        if (cartDto.getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Cart was empty"));
        }
        return ResponseEntity.ok(checkoutService.createOrder(cartDto));


        //var orderDto = orderService.createOrder(cartDto);

    }
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(Map<String, String> headers, @RequestBody String payload) {
        checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart was not found"));
    }
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto("Error with stripe payment"));
    }
}
