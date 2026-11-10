package com.placementpro.controller;

import com.placementpro.model.Submission;
import com.placementpro.repository.SubmissionRepository;
import com.placementpro.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @GetMapping
    public ResponseEntity<List<Submission>> getSubmissions(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        List<Submission> submissions = submissionRepository
                .findByUserOrderByCreatedAtDesc(userDetails.getId())
                .stream().limit(50).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(submissions);
    }
}

