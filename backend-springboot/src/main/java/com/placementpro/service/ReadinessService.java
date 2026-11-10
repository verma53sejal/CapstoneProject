package com.placementpro.service;

import com.placementpro.model.User;
import com.placementpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadinessService {

    @Autowired
    private UserRepository userRepository;

    public void recalculate(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        User.Stats stats = user.getStats();
        double dsaScore = Math.min(100, (stats.getTotalSolved() / 150.0) * 100);
        double aptScore = user.getAptitudeScore();
        double mockScore = Math.min(100, stats.getMockInterviewsDone() * 10.0);
        double resumeScore = user.isResumeUploaded() ? 100 : 0;
        double contestScore = Math.min(100, stats.getContestsParticipated() * 20.0);
        double streakScore = Math.min(100, stats.getStreak() * 3.0);

        int total = (int) Math.round(
            dsaScore * 0.35 + aptScore * 0.20 + mockScore * 0.20 +
            resumeScore * 0.10 + contestScore * 0.10 + streakScore * 0.05
        );

        String level = "Beginner";
        if (total >= 80) level = "Placement Ready";
        else if (total >= 65) level = "Interview Ready";
        else if (total >= 45) level = "Ready";
        else if (total >= 25) level = "Intermediate";

        user.setPlacementReadinessScore(total);
        user.setReadinessLevel(level);
        userRepository.save(user);
    }
}

