package com.placementpro.controller;

import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Object> result = dashboardService.getDashboard(userDetails.getId());
        return ResponseEntity.ok(result);
    }
}

