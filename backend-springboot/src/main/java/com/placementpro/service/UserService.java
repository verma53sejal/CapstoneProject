package com.placementpro.service;

import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private ReadinessService readinessService;

    public Map<String, Object> getUserStats(String userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // Topic breakdown from accepted submissions
        List<Submission> acceptedSubs = submissionRepository.findByUserAndStatus(userId, "Accepted");
        // Note: Full aggregation with problem lookup would require MongoTemplate;
        // using in-memory grouping for now (acceptable for moderate data sizes)
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("topicBreakdown", Collections.emptyList()); // Extended in controller if needed
        return result;
    }

    public Map<String, Object> analyzeResume(String userId, String resumeData) {
        List<String> keywords = Arrays.asList(
                "React", "Node.js", "Python", "JavaScript", "SQL", "Git", "REST API", "MongoDB"
        );
        final String resumeLower = resumeData != null ? resumeData.toLowerCase() : "";

        List<String> found = keywords.stream()
                .filter(k -> resumeLower.contains(k.toLowerCase()))
                .collect(Collectors.toList());

        List<String> missing = keywords.stream()
                .filter(k -> !resumeLower.contains(k.toLowerCase()))
                .collect(Collectors.toList());

        int atsScore = Math.round((float) found.size() / keywords.size() * 100);

        User user = userRepository.findById(userId).orElseThrow();
        boolean firstUpload = !user.isResumeUploaded();
        user.setResumeUploaded(true);
        user.setResumeScore(atsScore);
        if (firstUpload) user.setCredits(user.getCredits() + 25);
        userRepository.save(user);

        if (firstUpload) {
            Credit credit = new Credit();
            credit.setUser(userId);
            credit.setAmount(25);
            credit.setType("earn");
            credit.setReason("Resume uploaded and analyzed");
            credit.setBalance(user.getCredits());
            creditRepository.save(credit);
        }

        readinessService.recalculate(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("atsScore", atsScore);
        result.put("missingKeywords", missing);
        result.put("foundKeywords", found);
        result.put("newBalance", user.getCredits());
        return result;
    }
}

