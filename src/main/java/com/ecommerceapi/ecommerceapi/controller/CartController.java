package com.ecommerceapi.ecommerceapi.controller;

import com.ecommerceapi.ecommerceapi.dto.CartListDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/cartInfo")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping(value = "/details", params = "userId")
    @Operation(
            summary = "Get an cart details for a user",
            description = "Get an cart details for a user",
            tags = {"Cart Service"}
    )
    public ResponseEntity<ProductListDTO> findCartProductDetails(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.findCartProductDetails(userId));
    }

    @PostMapping(value = "/addToCart/user/{userId}")
    @Operation(
            summary = "Add products to the card based on the for a user",
            description = "Add products to the card based on the for a user",
            tags = {"Cart Service"}
    )
    public ResponseEntity<CartListDTO> addToCart(@RequestBody CartListDTO cartListDTO,
                             @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(cartService.addToCart(cartListDTO, userId));
    }
}
