package com.ecommerceapi.ecommerceapi.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

//@Entity
@Document(collection = "product")
public class Product {
    @Id
    @Field("id")
    private Long id;

    @Field("product_name")
    private String productName;

    @Field("product_id")
    private Long productId;

    @Field("description")
    private String description;

    @Field("price")
    private BigDecimal price;

    public Product() {
    }

    public Product(Long id, String productName, Long productId, String description, BigDecimal price) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Product setId(Long id) {
        this.id = id;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Long getProductId() {
        return productId;
    }

    public Product setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Product setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}

