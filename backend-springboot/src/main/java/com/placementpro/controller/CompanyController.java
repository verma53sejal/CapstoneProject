package com.placementpro.controller;

import com.placementpro.model.Company;
import com.placementpro.security.UserDetailsServiceImpl;
import com.placementpro.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getCompanyProgress(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, Object> result = companyService.getCompanyProgress(id, userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/select")
    public ResponseEntity<Map<String, String>> selectCompany(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetailsServiceImpl.UserDetailsImpl userDetails) {
        Map<String, String> result = companyService.selectCompany(id, userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        Company created = companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

