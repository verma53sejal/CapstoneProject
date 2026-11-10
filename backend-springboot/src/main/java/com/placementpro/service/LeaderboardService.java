package com.placementpro.service;

import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getLeaderboard(String userId, String type, int limit) {
        Sort sort;
        if ("streak".equals(type)) {
            sort = Sort.by(Sort.Direction.DESC, "stats.streak");
        } else if ("solved".equals(type)) {
            sort = Sort.by(Sort.Direction.DESC, "stats.totalSolved");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "credits");
        }

        List<User> allUsers = userRepository.findByRole("student", sort);
        List<User> topUsers = allUsers.stream().limit(limit).collect(Collectors.toList());

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            User u = topUsers.get(i);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("rank", i + 1);
            entry.put("_id", u.getId());
            entry.put("username", u.getUsername());
            entry.put("profile", u.getProfile());
            entry.put("stats", u.getStats());
            entry.put("credits", u.getCredits());
            entry.put("placementReadinessScore", u.getPlacementReadinessScore());
            entry.put("readinessLevel", u.getReadinessLevel());
            leaderboard.add(entry);
        }

        int myRank = 0;
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getId().equals(userId)) {
                myRank = i + 1;
                break;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("leaderboard", leaderboard);
        result.put("myRank", myRank);
        return result;
    }
}

