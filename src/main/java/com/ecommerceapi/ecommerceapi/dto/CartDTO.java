package com.ecommerceapi.ecommerceapi.dto;

public class CartDTO {

    private Long productId;
    private Long userId;
    private Integer quantity;

    public CartDTO(Long productId, Long userId, Integer quantity) {
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public CartDTO setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public CartDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public CartDTO setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
