package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.UserOrderDto;
import com.codewithmosh.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    UserOrderDto toDto(Order order);
}
