package com.codewithmosh.store.dtos;

import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class UserOrderDto {
    private Long id;
    private String status;
    private LocalDateTime date;
    private List<UserOrderItemDto> orderItems;
    private BigDecimal totalPrice;
}
