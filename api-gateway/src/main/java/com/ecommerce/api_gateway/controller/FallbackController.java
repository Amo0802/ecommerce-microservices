package com.ecommerce.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/products")
    public Mono<String> productsFallback() {
        return Mono.just("Product service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/orders")
    public Mono<String> ordersFallback() {
        return Mono.just("Order service is temporarily unavailable. Please try again later.");
    }
}