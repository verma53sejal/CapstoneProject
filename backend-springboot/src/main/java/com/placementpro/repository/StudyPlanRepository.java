package com.placementpro.repository;

import com.placementpro.model.StudyPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface StudyPlanRepository extends MongoRepository<StudyPlan, String> {
    Optional<StudyPlan> findFirstByUserOrderByCreatedAtDesc(String userId);
}

