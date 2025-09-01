package com.ecommerceapi.ecommerceapi.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductListDTO {

    private List<ProductDTO> products;

    private BigDecimal basketPrice;

    public List<ProductDTO> getProducts() {
        return products;
    }

    public ProductListDTO setProducts(List<ProductDTO> products) {
        this.products = products;
        return this;
    }

    public BigDecimal getBasketPrice() {
        return basketPrice;
    }

    public ProductListDTO setBasketPrice(BigDecimal basketPrice) {
        this.basketPrice = basketPrice;
        return this;
    }
}
