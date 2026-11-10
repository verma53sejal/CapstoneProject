package com.placementpro.repository;

import com.placementpro.model.MockInterview;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MockInterviewRepository extends MongoRepository<MockInterview, String> {
    List<MockInterview> findByUserAndCompletedOrderByCreatedAtDesc(String userId, boolean completed);
    List<MockInterview> findByUserOrderByCreatedAtDesc(String userId);
}

