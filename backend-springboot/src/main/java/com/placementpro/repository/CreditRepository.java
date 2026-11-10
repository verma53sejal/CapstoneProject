package com.placementpro.repository;

import com.placementpro.model.Credit;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CreditRepository extends MongoRepository<Credit, String> {
    List<Credit> findByUserOrderByCreatedAtDesc(String userId);
}

