package com.placementpro.service;

import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> getDashboard(String userId) {
        User user = userRepository.findById(userId).orElseThrow();

        List<Submission> recentSubmissions = submissionRepository
                .findByUserOrderByCreatedAtDesc(userId)
                .stream().limit(10).collect(Collectors.toList());

        // Activity heatmap - last 365 days
        Instant oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS);
        List<Submission> acceptedSubs = submissionRepository
                .findByUserAndStatusAndCreatedAtAfter(userId, "Accepted", oneYearAgo);

        Map<String, Long> activityMap = acceptedSubs.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCreatedAt().toString().substring(0, 10), // YYYY-MM-DD
                        Collectors.counting()
                ));

        List<Map<String, Object>> activityHeatmap = activityMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("_id", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("_id")))
                .collect(Collectors.toList());

        // Weekly activity - last 7 days
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<Submission> weekSubs = submissionRepository
                .findByUserAndCreatedAtAfter(userId, sevenDaysAgo);

        Map<String, List<Submission>> weeklyMap = weekSubs.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCreatedAt().toString().substring(0, 10)
                ));

        List<Map<String, Object>> weeklyActivity = weeklyMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("_id", e.getKey());
                    m.put("count", (long) e.getValue().size());
                    m.put("accepted", e.getValue().stream().filter(s -> "Accepted".equals(s.getStatus())).count());
                    return m;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("_id")))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("recentSubmissions", recentSubmissions);
        result.put("activityHeatmap", activityHeatmap);
        result.put("weeklyActivity", weeklyActivity);
        return result;
    }
}

