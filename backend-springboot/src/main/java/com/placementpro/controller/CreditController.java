package com.placementpro.controller;

import com.placementpro.model.Credit;
import com.placementpro.repository.CreditRepository;
import com.placementpro.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditRepository creditRepository;

    @GetMapping("/history")
    public ResponseEntity<List<Credit>> getCreditHistory(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        List<Credit> history = creditRepository.findByUserOrderByCreatedAtDesc(userDetails.getId())
                .stream().limit(50).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(history);
    }
}

