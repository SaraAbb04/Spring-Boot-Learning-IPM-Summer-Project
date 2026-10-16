package com.summer_project.demo.repository;

import com.summer_project.demo.model.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends MongoRepository<Plan, String> {
    Optional<Plan> findByName(String name);
    List<Plan> findByActiveTrue();
}
