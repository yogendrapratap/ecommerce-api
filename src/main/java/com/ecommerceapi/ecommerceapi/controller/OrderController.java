package com.ecommerceapi.ecommerceapi.controller;

import com.ecommerceapi.ecommerceapi.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController("/placeOrder")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Place order for a user",
            description = "Place order for a user",
            tags = {"Place Order Service"}
    )
    public ResponseEntity<String> placeOrder(@RequestParam("userId") Long userId) throws ExecutionException, JsonProcessingException, InterruptedException {
        orderService.placeOrder(userId);
        return  ResponseEntity.ok().body("Order placed successfully");
    }
}
