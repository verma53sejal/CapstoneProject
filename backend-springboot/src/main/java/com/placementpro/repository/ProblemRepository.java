package com.placementpro.repository;

import com.placementpro.model.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface ProblemRepository extends MongoRepository<Problem, String> {
    Optional<Problem> findBySlug(String slug);
    List<Problem> findByIsDailyTrue();
    boolean existsBySlug(String slug);
    Page<Problem> findByDifficulty(String difficulty, Pageable pageable);
    Page<Problem> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    long countByDifficulty(String difficulty);
}

