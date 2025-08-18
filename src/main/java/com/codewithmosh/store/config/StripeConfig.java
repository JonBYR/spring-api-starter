package com.codewithmosh.store.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

public class StripeConfig {
    @Value("${stripe.secretKey}")
    private String key;

    @PostConstruct
    public void init() {
        Stripe.apiKey = key;
    }
}
