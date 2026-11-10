package com.placementpro.repository;

import com.placementpro.model.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends MongoRepository<Submission, String> {
    List<Submission> findByUserOrderByCreatedAtDesc(String userId);
    List<Submission> findByUserAndProblemOrderByCreatedAtDesc(String userId, String problemId);
    Optional<Submission> findByUserAndProblemAndStatus(String userId, String problemId, String status);
    List<Submission> findByUserAndStatus(String userId, String status);
    List<String> findDistinctProblemByUserAndStatus(String userId, String status);
    long countByUser(String userId);
    long countByStatus(String status);
    // For activity heatmap
    List<Submission> findByUserAndStatusAndCreatedAtAfter(String userId, String status, Instant after);
    List<Submission> findByUserAndCreatedAtAfter(String userId, Instant after);
}

