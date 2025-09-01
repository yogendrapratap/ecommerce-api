package com.ecommerceapi.ecommerceapi.controller;

import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductSearchController {

    @Autowired
    private ProductService productService;

    @GetMapping(value = "/search/products", params = "productName")
    @Operation(
            summary = "Get an product details by product name",
            description = "Get an product details by product name",
            tags = {"Product Service"}
    )
    public ResponseEntity<ProductListDTO> search(@RequestParam String productName) {
       return ResponseEntity.ok(productService.search(productName));
    }

    @GetMapping(value = "/search/all/products")
    @Operation(
            summary = "Search products in the system with pagination",
            description = "Get an cart details for a user with pagination",
            tags = {"Product Service"}
    )
    @Parameters(
            {
                    @Parameter(name = "pageNumber", description = "Page number for pagination", required = true, example = "0"),
                    @Parameter(name = "pageSize", description = "Number of records per page", required = true, example = "10")
            }
    )
    public ResponseEntity<ProductListDTO> searchProducts(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return ResponseEntity.ok(productService.searchProducts(pageNumber, pageSize));
    }

}
