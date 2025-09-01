package com.ecommerceapi.ecommerceapi.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CART")
public class Cart {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userId;
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public Cart setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getProductId() {
        return productId;
    }

    public Cart setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Cart setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Cart setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
