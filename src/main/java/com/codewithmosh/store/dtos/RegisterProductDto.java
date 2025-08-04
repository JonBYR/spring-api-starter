package com.codewithmosh.store.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterProductDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Byte categoryId;
}
