package com.ecommerceapi.ecommerceapi.repository;

import com.ecommerceapi.ecommerceapi.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {

    Product findByProductName(String productName);

    List<Product> findAllByProductIdIn(List<Long> productIds);

    List<Product> findByProductNameLikeIgnoreCase(String productName);

    Product findAllByProductId(Long productId);
}
