package com.ecommerceapi.ecommerceapi.dto;

import java.math.BigDecimal;

public class ProductDTO {

    private Integer id;
    private String productName;
    private String productId;
    private String description;
    private BigDecimal price;
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public ProductDTO setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public ProductDTO setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public ProductDTO setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductDTO setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ProductDTO setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
