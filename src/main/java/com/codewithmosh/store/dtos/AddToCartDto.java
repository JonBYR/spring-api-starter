package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartDto {
    @NotNull(message = "product id must not be null")
    private Long productId;
}
