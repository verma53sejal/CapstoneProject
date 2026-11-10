package com.placementpro.repository;

import com.placementpro.model.Reward;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RewardRepository extends MongoRepository<Reward, String> {
    List<Reward> findByAvailableTrue();
}

