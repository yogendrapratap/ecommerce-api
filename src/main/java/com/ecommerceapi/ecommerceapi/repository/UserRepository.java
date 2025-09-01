package com.ecommerceapi.ecommerceapi.repository;

import com.ecommerceapi.ecommerceapi.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends MongoRepository<User, Long> {

    User findByUsernameAndPassword(String username, String password);
}
