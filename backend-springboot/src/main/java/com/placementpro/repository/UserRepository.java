package com.placementpro.repository;

import com.placementpro.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    java.util.List<User> findByRoleOrderByCreditsDesc(String role);
    java.util.List<User> findByRoleOrderByStatsTotalSolvedDesc(String role);
    java.util.List<User> findByRoleOrderByStatsStreakDesc(String role);
    java.util.List<User> findByRole(String role, org.springframework.data.domain.Sort sort);
    long countByRole(String role);
}

