package com.placementpro.repository;

import com.placementpro.model.Redemption;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RedemptionRepository extends MongoRepository<Redemption, String> {
    List<Redemption> findByUser(String userId);
}

