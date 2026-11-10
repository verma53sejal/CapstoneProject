package com.placementpro.service;

import com.placementpro.exception.BadRequestException;
import com.placementpro.exception.ResourceNotFoundException;
import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditRepository creditRepository;

    public Map<String, Object> getRewards(String userId) {
        List<Reward> rewards = rewardRepository.findByAvailableTrue();
        List<Redemption> myRedemptions = redemptionRepository.findByUser(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("rewards", rewards);
        result.put("myRedemptions", myRedemptions);
        return result;
    }

    public Map<String, Object> redeemReward(String rewardId, String userId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getCredits() < reward.getCreditCost()) {
            throw new BadRequestException("Insufficient credits");
        }

        user.setCredits(user.getCredits() - reward.getCreditCost());
        userRepository.save(user);

        reward.setTotalRedeemed(reward.getTotalRedeemed() + 1);
        rewardRepository.save(reward);

        Credit credit = new Credit();
        credit.setUser(userId);
        credit.setAmount(-reward.getCreditCost());
        credit.setType("spend");
        credit.setReason("Redeemed: " + reward.getTitle());
        credit.setBalance(user.getCredits());
        creditRepository.save(credit);

        Redemption redemption = new Redemption();
        redemption.setUser(userId);
        redemption.setReward(rewardId);
        redemption.setCreditsSpent(reward.getCreditCost());
        redemptionRepository.save(redemption);

        Map<String, Object> result = new HashMap<>();
        result.put("redemption", redemption);
        result.put("newBalance", user.getCredits());
        return result;
    }

    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }
}

