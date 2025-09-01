package com.ecommerceapi.ecommerceapi.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "orders")
public class Orders {

    @Id
    private String id;

    private List<Order> orders;

    public  Orders() {
    }

    public Orders(String id, List<Order> orders) {
        this.id = id;
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public Orders setId(String id) {
        this.id = id;
        return this;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Orders setOrders(List<Order> orders) {
        this.orders = orders;
        return this;
    }
}
