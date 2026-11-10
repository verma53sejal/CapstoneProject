package com.placementpro.controller;

import com.placementpro.dto.SubmitSolutionRequest;
import com.placementpro.model.Problem;
import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProblems(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> result = problemService.getProblems(
                userDetails.getId(), difficulty, tags, company, search, page, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyChallenge(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        return ResponseEntity.ok(problemService.getDailyChallenge(userDetails.getId()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Map<String, Object>> getProblem(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Object> result = problemService.getProblemBySlug(slug, userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitSolution(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestBody SubmitSolutionRequest request) {
        Map<String, Object> result = problemService.submitSolution(id, userDetails.getId(), request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<Map<String, Object>> runSolution(
            @PathVariable String id,
            @RequestBody SubmitSolutionRequest request) {
        return ResponseEntity.ok(problemService.runSolution(id, request));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Map<String, Boolean>> bookmarkProblem(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Boolean> result = problemService.bookmarkProblem(id, userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Problem> createProblem(@RequestBody Problem problem) {
        Problem created = problemService.createProblem(problem);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

