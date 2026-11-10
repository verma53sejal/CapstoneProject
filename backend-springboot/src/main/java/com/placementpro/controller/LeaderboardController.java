package com.placementpro.controller;

import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLeaderboard(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "credits") String type,
            @RequestParam(defaultValue = "50") int limit) {
        Map<String, Object> result = leaderboardService.getLeaderboard(userDetails.getId(), type, limit);
        return ResponseEntity.ok(result);
    }
}

