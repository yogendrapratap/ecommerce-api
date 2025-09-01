package com.ecommerceapi.ecommerceapi.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "orders")
public class Orders {

    @Id
    private String id;

    private Long orderNumber;

    private String status;

    private BigDecimal totalOrderPrice;

    private List<Order> orders;

    public  Orders() {
    }

    public Orders(String status, Long orderNumber, BigDecimal totalOrderPrice, List<Order> orders) {
        this.status = status;
        this.orderNumber = orderNumber;
        this.totalOrderPrice = totalOrderPrice;
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public Orders setId(String id) {
        this.id = id;
        return this;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public Orders setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public BigDecimal getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public Orders setTotalOrderPrice(BigDecimal totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Orders setStatus(String status) {
        this.status = status;
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
