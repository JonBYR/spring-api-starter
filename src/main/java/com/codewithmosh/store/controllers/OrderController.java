package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.dtos.UserOrderDto;
import com.codewithmosh.store.exceptions.OrderIsNotAuth;
import com.codewithmosh.store.exceptions.OrderNotFoundException;
import com.codewithmosh.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<UserOrderDto> allOrders() {
        return orderService.allOrders();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable(name = "id") Long id) {
        var orderDto = orderService.findOrderById(id);
        return ResponseEntity.ok(orderDto);
    }
    @ExceptionHandler(OrderIsNotAuth.class)
    public ResponseEntity<ErrorDto> handleOrderIsNotAuth() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto("Order is not auth user!"));
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDto> handleOrderNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("Order not found!"));
    }
}
