package com.ecommerceapi.ecommerceapi.repository;

import com.ecommerceapi.ecommerceapi.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<Cart, Long> {
    List<Cart> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
