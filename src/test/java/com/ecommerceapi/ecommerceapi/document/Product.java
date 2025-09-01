package com.ecommerceapi.ecommerceapi.document;

import java.time.LocalDate;

public class Product {

    private Integer quantity;
    private Long productId;
    private String productName;
    private LocalDate deliveryDate;

    public Product() {
    }

    public Product(Integer quantity, Long productId, String productName, LocalDate deliveryDate) {
        this.quantity = quantity;
        this.productId = productId;
        this.productName = productName;
        this.deliveryDate = deliveryDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Product setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public Long getProductId() {
        return productId;
    }

    public Product setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public Product setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }
}
