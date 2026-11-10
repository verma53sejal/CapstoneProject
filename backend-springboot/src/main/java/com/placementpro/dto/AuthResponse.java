package com.placementpro.dto;

import com.placementpro.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String id;
    private String username;
    private String email;
    private String role;
    private int credits;
    private User.Profile profile;
    private User.Stats stats;
    private double placementReadinessScore;
    private String readinessLevel;
    private String token;

    public AuthResponse(User user, String token) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.credits = user.getCredits();
        this.profile = user.getProfile();
        this.stats = user.getStats();
        this.placementReadinessScore = user.getPlacementReadinessScore();
        this.readinessLevel = user.getReadinessLevel();
        this.token = token;
    }
}

