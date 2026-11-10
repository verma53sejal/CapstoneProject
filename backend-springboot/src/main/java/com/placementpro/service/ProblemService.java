package com.placementpro.service;

import com.placementpro.dto.SubmitSolutionRequest;
import com.placementpro.exception.BadRequestException;
import com.placementpro.exception.ResourceNotFoundException;
import com.placementpro.model.Credit;
import com.placementpro.model.Problem;
import com.placementpro.model.Submission;
import com.placementpro.model.User;
import com.placementpro.repository.CreditRepository;
import com.placementpro.repository.ProblemRepository;
import com.placementpro.repository.SubmissionRepository;
import com.placementpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private ReadinessService readinessService;

    @Autowired
    private Judge0Service judge0Service;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> getProblems(String userId, String difficulty, String tags,
                                            String company, String search, int page, int limit) {
        Query query = new Query();
        if (difficulty != null && !difficulty.isEmpty()) {
            query.addCriteria(Criteria.where("difficulty").is(difficulty));
        }
        if (tags != null && !tags.isEmpty()) {
            List<String> tagList = Arrays.asList(tags.split(","));
            query.addCriteria(Criteria.where("tags").in(tagList));
        }
        if (company != null && !company.isEmpty()) {
            query.addCriteria(Criteria.where("companies").in(company));
        }
        if (search != null && !search.isEmpty()) {
            query.addCriteria(Criteria.where("title").regex(search, "i"));
        }

        long total = mongoTemplate.count(query, Problem.class);
        query.with(Sort.by(Sort.Direction.ASC, "order"))
             .skip((long) (page - 1) * limit)
             .limit(limit);

        List<Problem> problems = mongoTemplate.find(query, Problem.class);

        // Mark solved status for current user
        List<Submission> acceptedSubs = submissionRepository.findByUserAndStatus(userId, "Accepted");
        Set<String> solvedSet = acceptedSubs.stream()
                .map(Submission::getProblem)
                .collect(Collectors.toSet());

        List<Map<String, Object>> problemsWithStatus = problems.stream().map(p -> {
            Map<String, Object> map = problemToMap(p);
            map.put("solved", solvedSet.contains(p.getId()));
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("problems", problemsWithStatus);
        result.put("total", total);
        result.put("pages", (int) Math.ceil((double) total / limit));
        return result;
    }

    public Map<String, Object> getProblemBySlug(String slug, String userId) {
        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        boolean solved = submissionRepository.findByUserAndProblemAndStatus(userId, problem.getId(), "Accepted").isPresent();
        List<Submission> userSubmissions = submissionRepository
                .findByUserAndProblemOrderByCreatedAtDesc(userId, problem.getId())
                .stream().limit(5).collect(Collectors.toList());

        Map<String, Object> result = problemToMap(problem);
        result.put("solved", solved);
        result.put("userSubmissions", userSubmissions);
        return result;
    }

    public Map<String, Object> getDailyChallenge(String userId) {
        Problem problem = problemRepository.findByIsDailyTrue().stream().findFirst()
                .orElseGet(() -> problemRepository.findAll(Sort.by(Sort.Direction.ASC, "order"))
                        .stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No problems available")));
        return getProblemBySlug(problem.getSlug(), userId);
    }

    public Map<String, Object> submitSolution(String problemId, String userId, SubmitSolutionRequest request) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        List<Problem.TestCase> testCases = problem.getTestCases() == null ? List.of() : problem.getTestCases();
        if (testCases.isEmpty()) {
            throw new BadRequestException("This problem has no executable test cases configured; no submission was marked correct or credited.");
        }
        Judge0Service.ExecutionResult execution = null;
        int passed = 0;
        for (Problem.TestCase testCase : testCases) {
            execution = judge0Service.execute(request.getLanguage(), request.getCode(), testCase.getStdin(), testCase.getExpectedOutput());
            if ("Accepted".equals(execution.status())) passed++;
            else break;
        }
        String status = execution == null ? "Runtime Error" : execution.status();
        if (passed == testCases.size()) status = "Accepted";

        boolean alreadySolved = submissionRepository
                .findByUserAndProblemAndStatus(userId, problem.getId(), "Accepted").isPresent();

        Submission submission = new Submission();
        submission.setUser(userId);
        submission.setProblem(problem.getId());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage() != null ? request.getLanguage() : "javascript");
        submission.setStatus(status);
        submission.setRuntime(execution == null || execution.time() == null ? null : Math.round(Double.parseDouble(execution.time()) * 1000));
        submission.setMemory(execution == null ? null : execution.memory());
        submission = submissionRepository.save(submission);

        // Update problem stats
        problem.setTotalSubmissions(problem.getTotalSubmissions() + 1);
        if ("Accepted".equals(status)) {
            problem.setCorrectSubmissions(problem.getCorrectSubmissions() + 1);
        }
        if (problem.getTotalSubmissions() > 0) {
            problem.setAcceptanceRate((double) problem.getCorrectSubmissions() / problem.getTotalSubmissions() * 100);
        }
        problemRepository.save(problem);

        int creditsEarned = 0;
        if ("Accepted".equals(status) && !alreadySolved) {
            Map<String, Integer> creditMap = Map.of("Easy", 10, "Medium", 20, "Hard", 40);
            creditsEarned = creditMap.getOrDefault(problem.getDifficulty(), 10);

            User user = userRepository.findById(userId).orElseThrow();
            user.setCredits(user.getCredits() + creditsEarned);

            User.Stats stats = user.getStats();
            stats.setTotalSolved(stats.getTotalSolved() + 1);
            if ("Easy".equals(problem.getDifficulty())) stats.setEasySolved(stats.getEasySolved() + 1);
            else if ("Medium".equals(problem.getDifficulty())) stats.setMediumSolved(stats.getMediumSolved() + 1);
            else if ("Hard".equals(problem.getDifficulty())) stats.setHardSolved(stats.getHardSolved() + 1);
            user.setStats(stats);
            userRepository.save(user);

            Credit credit = new Credit();
            credit.setUser(userId);
            credit.setAmount(creditsEarned);
            credit.setType("earn");
            credit.setReason("Solved " + problem.getDifficulty() + " problem: " + problem.getTitle());
            credit.setBalance(user.getCredits());
            creditRepository.save(credit);

            readinessService.recalculate(userId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("submission", submission);
        result.put("creditsEarned", creditsEarned);
        result.put("status", status);
        result.put("passed", passed);
        result.put("total", testCases.size());
        result.put("stdout", execution == null ? null : execution.stdout());
        result.put("stderr", execution == null ? null : execution.stderr());
        result.put("compileOutput", execution == null ? null : execution.compileOutput());
        result.put("runtime", execution == null ? null : execution.time());
        result.put("memory", execution == null ? null : execution.memory());
        result.put("message", "Accepted".equals(request.getStatus()) ? "🎉 Accepted!" : "Try again!");
        result.put("message", "Accepted".equals(status) ? "Accepted" : "Submission did not pass all configured test cases.");
        return result;
    }

    public Map<String, Object> runSolution(String problemId, SubmitSolutionRequest request) {
        problemRepository.findById(problemId).orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        Judge0Service.ExecutionResult execution = judge0Service.execute(
                request.getLanguage(), request.getCode(), request.getStdin(), null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", execution.status());
        result.put("stdout", execution.stdout());
        result.put("stderr", execution.stderr());
        result.put("compileOutput", execution.compileOutput());
        result.put("runtime", execution.time());
        result.put("memory", execution.memory());
        result.put("message", "Run results are not submissions and do not award credits.");
        return result;
    }

    public Map<String, Boolean> bookmarkProblem(String problemId, String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<String> bookmarks = user.getBookmarks();
        boolean isBookmarked = bookmarks.contains(problemId);

        if (isBookmarked) {
            bookmarks.remove(problemId);
        } else {
            bookmarks.add(problemId);
        }
        user.setBookmarks(bookmarks);
        userRepository.save(user);

        return Map.of("bookmarked", !isBookmarked);
    }

    public Problem createProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    private Map<String, Object> problemToMap(Problem p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("_id", p.getId());
        map.put("id", p.getId());
        map.put("title", p.getTitle());
        map.put("slug", p.getSlug());
        map.put("description", p.getDescription());
        map.put("difficulty", p.getDifficulty());
        map.put("tags", p.getTags());
        map.put("companies", p.getCompanies());
        map.put("examples", p.getExamples());
        map.put("constraints", p.getConstraints());
        map.put("hints", p.getHints());
        map.put("credits", p.getCredits());
        map.put("acceptanceRate", p.getAcceptanceRate());
        map.put("totalSubmissions", p.getTotalSubmissions());
        map.put("topic", p.getTopic());
        map.put("order", p.getOrder());
        map.put("isDaily", p.isDaily());
        map.put("isPremium", p.isPremium());
        map.put("createdAt", p.getCreatedAt());
        return map;
    }
}

