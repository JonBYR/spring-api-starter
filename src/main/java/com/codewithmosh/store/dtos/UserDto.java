package com.codewithmosh.store.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {
    @JsonProperty("user_id") //renames property in JSON object
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL) //ensures field is included only is not null
    private String name;
    private String email;
}
