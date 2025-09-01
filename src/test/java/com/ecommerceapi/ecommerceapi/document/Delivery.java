package com.ecommerceapi.ecommerceapi.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "delivery")
public class Delivery {
    @Id
    private String id;
    private String orderId;
    private List<Product> products;
    private String deliveryStatus;
    private String deliveryAddress;

    public Delivery() {
    }

    public Delivery(String orderId, List<Product> products, String deliveryStatus, String deliveryAddress) {
        this.orderId = orderId;
        this.products = products;
        this.deliveryStatus = deliveryStatus;
        this.deliveryAddress = deliveryAddress;
    }

    public String getId() {
        return id;
    }

    public Delivery setId(String id) {
        this.id = id;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public Delivery setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Delivery setProducts(List<Product> products) {
        this.products = products;
        return this;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public Delivery setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
        return this;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Delivery setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }
}
