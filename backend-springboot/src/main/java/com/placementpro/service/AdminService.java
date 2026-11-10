package com.placementpro.service;

import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Map<String, Object> getAnalytics() {
        long totalUsers = userRepository.countByRole("student");
        long totalProblems = problemRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalCompanies = companyRepository.count();

        List<User> recentUsers = userRepository.findByRole("student",
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                .stream().limit(10).collect(Collectors.toList());

        // Top problems by submission count (simplified)
        // Full aggregation pipeline can be added via MongoTemplate if needed

        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("totalProblems", totalProblems);
        result.put("totalSubmissions", totalSubmissions);
        result.put("totalCompanies", totalCompanies);
        result.put("recentUsers", recentUsers);
        return result;
    }

    public List<User> getAllStudents() {
        return userRepository.findByRole("student",
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }
}

