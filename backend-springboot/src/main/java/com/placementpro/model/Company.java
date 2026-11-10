package com.placementpro.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "companies")
public class Company {

    @Id
    private String id;

    private String name;
    private String logo;
    private String tier = "Product"; // FAANG | Top MNC | Product | Service | Startup
    private List<String> requiredTopics = new ArrayList<>();
    private String avgPackage;
    private List<String> problems = new ArrayList<>(); // problem IDs
    private List<String> interviewProcess = new ArrayList<>();
    private String difficulty = "Medium"; // Easy | Medium | Hard

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

