package com.codewithmosh.store.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserOrderItemDto {
    private CartProductDto product;
    private int quantity;
    private BigDecimal totalPrice;
}
