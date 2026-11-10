package com.placementpro.controller;

import com.placementpro.model.Reward;
import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRewards(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Object> result = rewardService.getRewards(userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/redeem")
    public ResponseEntity<Map<String, Object>> redeemReward(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Object> result = rewardService.redeemReward(id, userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Reward> createReward(@RequestBody Reward reward) {
        Reward created = rewardService.createReward(reward);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

