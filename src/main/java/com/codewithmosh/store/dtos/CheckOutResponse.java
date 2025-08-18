package com.codewithmosh.store.dtos;

import lombok.Data;

@Data
public class CheckOutResponse {
    private Long id;
    private String checkoutUrl;
    public CheckOutResponse(Long id, String url) {
        this.id = id;
        this.checkoutUrl = url;
    }
}
