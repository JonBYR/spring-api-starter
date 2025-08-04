package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.dtos.UpdateUserDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserDto userDto);
    void update(UpdateUserDto updateUserDto, @MappingTarget User user); //updates the user passed in the argument with the
    //UpdateUserDto information
}
