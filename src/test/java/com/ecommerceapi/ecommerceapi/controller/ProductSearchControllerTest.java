package com.ecommerceapi.ecommerceapi.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;



import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductSearchController.class)
class ProductSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void testSearchByProductName() throws Exception {

        ProductDTO product = new ProductDTO()
                .setId(1L)
                .setProductId("PRD001")
                .setProductName("Samsung TV")
                .setDescription("Smart LED TV")
                .setPrice(new BigDecimal("49999.00"));

        ProductListDTO productList = new ProductListDTO()
                .setProducts(Collections.singletonList(product))
                .setBasketPrice(product.getPrice());

        Mockito.when(productService.search(anyString()))
                .thenReturn(productList);

        mockMvc.perform(get("/search/products")
                        .param("productName", "Samsung TV")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].productId").value("PRD001"))
                .andExpect(jsonPath("$.products[0].productName").value("Samsung TV"))
                .andExpect(jsonPath("$.basketPrice").value(49999.00));
    }

    @Test
    void testSearchProductsWithPagination() throws Exception {
        // Given
        ProductDTO product = new ProductDTO()
                .setId(2L)
                .setProductId("PAG001")
                .setProductName("Laptop")
                .setDescription("High-end gaming laptop")
                .setPrice(new BigDecimal("150000"));

        ProductListDTO productList = new ProductListDTO()
                .setProducts(Collections.singletonList(product))
                .setBasketPrice(product.getPrice());

        Mockito.when(productService.searchProducts(0, 10)).thenReturn(productList);

        mockMvc.perform(get("/search/all/products")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.products[0].productId").value("PAG001"))
                .andExpect(jsonPath("$.basketPrice").value(150000));
    }

}

