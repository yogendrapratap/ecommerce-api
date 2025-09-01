package com.ecommerceapi.ecommerceapi.controller;

import com.ecommerceapi.ecommerceapi.dto.CartListDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFindCartProductDetails() throws Exception {
        ProductDTO product = new ProductDTO()
                .setId(1)
                .setProductId("PRD001")
                .setProductName("Shoes")
                .setDescription("Running shoes")
                .setPrice(new BigDecimal("2999"));

        ProductListDTO productListDTO = new ProductListDTO()
                .setProducts(Collections.singletonList(product))
                .setBasketPrice(product.getPrice());

        Mockito.when(cartService.findCartProductDetails(101L)).thenReturn(productListDTO);

        mockMvc.perform(get("/cartInfo/details")
                        .param("userId", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].productId").value("PRD001"))
                .andExpect(jsonPath("$.basketPrice").value(2999));
    }

    @Test
    void testAddToCart() throws Exception {
        CartListDTO cartListDTO = new CartListDTO(); // mock empty or build as needed

        Mockito.when(cartService.addToCart(any(CartListDTO.class), eq(101L)))
                .thenReturn(cartListDTO);

        mockMvc.perform(post("/cartInfo/addToCart/user/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartListDTO)))
                .andExpect(status().isOk());
    }
}

