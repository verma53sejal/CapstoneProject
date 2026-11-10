package com.placementpro.controller;

import com.placementpro.model.StudyPlan;
import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/study-plans")
public class StudyPlanController {

    @Autowired
    private StudyPlanService studyPlanService;

    @PostMapping("/generate")
    public ResponseEntity<StudyPlan> generatePlan(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails,
            @RequestBody(required = false) Map<String, Object> body) {
        Integer studyHours = null;
        if (body != null && body.containsKey("studyHours")) {
            studyHours = ((Number) body.get("studyHours")).intValue();
        }
        StudyPlan plan = studyPlanService.generatePlan(userDetails.getId(), studyHours);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPlan(
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Optional<StudyPlan> plan = studyPlanService.getCurrentPlan(userDetails.getId());
        return ResponseEntity.ok(plan.orElse(null));
    }

    @PutMapping("/{id}/task")
    public ResponseEntity<StudyPlan> updateTask(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        int dayIndex = ((Number) body.get("dayIndex")).intValue();
        int taskIndex = ((Number) body.get("taskIndex")).intValue();
        boolean completed = (boolean) body.get("completed");
        StudyPlan plan = studyPlanService.updateTask(id, dayIndex, taskIndex, completed);
        return ResponseEntity.ok(plan);
    }
}

