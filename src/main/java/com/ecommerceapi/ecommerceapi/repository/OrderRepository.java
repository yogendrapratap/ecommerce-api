package com.ecommerceapi.ecommerceapi.repository;

import com.ecommerceapi.ecommerceapi.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {
}
