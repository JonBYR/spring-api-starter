package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartDto {
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity can be no greater than 100")
    private int quantity;
}
