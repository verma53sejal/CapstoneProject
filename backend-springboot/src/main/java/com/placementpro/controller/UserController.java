package com.placementpro.controller;

import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Object> result = userService.getUserStats(userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<Map<String, Object>> uploadResume(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestBody Map<String, String> body) {
        String resumeData = body.get("resumeData");
        Map<String, Object> result = userService.analyzeResume(userDetails.getId(), resumeData);
        return ResponseEntity.ok(result);
    }
}

