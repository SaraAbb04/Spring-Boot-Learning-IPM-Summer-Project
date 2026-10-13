package com.summer_project.demo.repository;

import com.summer_project.demo.model.ModelPricing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ModelPricingRepository extends MongoRepository<ModelPricing, String> {
    Optional<ModelPricing> findByModelAndActiveTrue(String model);
    List<ModelPricing> findAllByModelAndActiveTrue(String model);
}
