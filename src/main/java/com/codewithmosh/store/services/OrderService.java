package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UserOrderDto;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.exceptions.OrderIsNotAuth;
import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor //will inject interfaces that are final, while value annotation will inject string value
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    @Value("${websiteUrl}")
    private String websiteUrl;
    public Long createOrder(CartDto cartDto) {
        var id = cartDto.getId();
        var cart = cartRepository.findById(id).orElse(null);
        var order = Order.fromCart(cart, authService.returnAuthUser());
        orderRepository.save(order);

        cartService.clearList(cart.getId());
        return order.getId();
    }
    public List<UserOrderDto> allOrders() {
        var user = authService.returnAuthUser();
        System.out.println("Calling orders");
        List<Order> orders = orderRepository.findAllByCustomer(user);
        var dtos = orders.stream().map(orderMapper::toDto).toList();
        return dtos;
    }
    public UserOrderDto findOrderById(Long id) {
        var order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        var user = authService.returnAuthUser();
        var orderUser = orderRepository.findAllByCustomer(user);
        if(!user.isAuthUser(orderUser.getFirst().getCustomerId())) {
            throw new OrderIsNotAuth();
        }
        return orderMapper.toDto(order);
    }
}
