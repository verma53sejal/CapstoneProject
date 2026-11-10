package com.placementpro.service;

import com.placementpro.exception.ResourceNotFoundException;
import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Map<String, Object> getCompanyProgress(String companyId, String userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        List<String> problemIds = company.getProblems();
        List<Problem> problems = problemRepository.findAllById(problemIds);

        List<Submission> acceptedSubs = submissionRepository.findByUserAndStatus(userId, "Accepted");
        Set<String> solvedSet = acceptedSubs.stream()
                .map(Submission::getProblem).collect(Collectors.toSet());

        List<Map<String, Object>> progress = problems.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("_id", p.getId());
            m.put("title", p.getTitle());
            m.put("difficulty", p.getDifficulty());
            m.put("slug", p.getSlug());
            m.put("solved", solvedSet.contains(p.getId()));
            return m;
        }).collect(Collectors.toList());

        long solvedCount = progress.stream().filter(m -> Boolean.TRUE.equals(m.get("solved"))).count();
        int percentage = problems.isEmpty() ? 0
                : (int) Math.round((double) solvedCount / problems.size() * 100);

        Map<String, Object> result = new HashMap<>();
        result.put("company", company);
        result.put("problems", progress);
        result.put("solvedCount", solvedCount);
        result.put("totalCount", problems.size());
        result.put("percentage", percentage);
        return result;
    }

    public Map<String, String> selectCompany(String companyId, String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<String> targets = user.getTargetCompanies();
        if (!targets.contains(companyId)) {
            targets.add(companyId);
            user.setTargetCompanies(targets);
            userRepository.save(user);
        }
        return Map.of("message", "Company added to targets");
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }
}

