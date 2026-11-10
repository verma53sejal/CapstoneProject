package com.placementpro.controller;

import com.placementpro.dto.MockInterviewSubmitRequest;
import com.placementpro.model.MockInterview;
import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.MockInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mock-interviews")
public class MockInterviewController {

    @Autowired
    private MockInterviewService mockInterviewService;

    @PostMapping("/start")
    public ResponseEntity<MockInterview> startInterview(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestBody(required = false) Map<String, String> body) {
        String type = (body != null && body.containsKey("type")) ? body.get("type") : "Mixed";
        MockInterview interview = mockInterviewService.startInterview(userDetails.getId(), type);
        return ResponseEntity.ok(interview);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<MockInterview> submitInterview(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestBody MockInterviewSubmitRequest request) {
        MockInterview interview = mockInterviewService.submitInterview(id, userDetails.getId(), request.getAnswers());
        return ResponseEntity.ok(interview);
    }

    @GetMapping("/history")
    public ResponseEntity<List<MockInterview>> getHistory(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        List<MockInterview> history = mockInterviewService.getHistory(userDetails.getId());
        return ResponseEntity.ok(history);
    }
}

