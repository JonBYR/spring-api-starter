package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CheckOutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.exceptions.SignatureException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;
    @Value("${websiteUrl}")
    private String websiteUrl;

    @Transactional
    public CheckOutResponse createOrder(CartDto cartDto) {
        var id = cartDto.getId();
        var cart = cartRepository.findById(id).orElse(null);
        var order = Order.fromCart(cart, authService.returnAuthUser());
        orderRepository.save(order);
        try {
            //Create checkout session
            var session = paymentGateway.createSession(order);
            cartService.clearList(cart.getId());
            return new CheckOutResponse(order.getId(), session.getUrl());
        }
        catch (PaymentException e) {
            orderRepository.delete(order);
            throw e;
        }
    }
    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway.parseWebhookRequest(request).ifPresent(paymentResult -> {
            var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
            order.setOrderStatus(paymentResult.getResult());
            orderRepository.save(order);
        });
    }

}
