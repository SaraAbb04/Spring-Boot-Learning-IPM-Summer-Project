package com.summer_project.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.summer_project.demo.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
