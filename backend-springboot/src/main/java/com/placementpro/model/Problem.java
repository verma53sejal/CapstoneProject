package com.placementpro.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "problems")
public class Problem {

    @Id
    private String id;

    private String title;

    @Indexed(unique = true)
    private String slug;

    private String description;
    private String difficulty; // Easy | Medium | Hard
    private List<String> tags = new ArrayList<>();
    private List<String> companies = new ArrayList<>(); // company IDs

    private List<Example> examples = new ArrayList<>();
    // Executable judge cases are managed by the backend. Hidden cases are never
    // included in the public problem response.
    private List<TestCase> testCases = new ArrayList<>();
    private List<String> constraints = new ArrayList<>();
    private List<String> hints = new ArrayList<>();

    private String solution;
    private String solutionExplanation;
    private int credits = 10;
    private double acceptanceRate = 0;
    private int totalSubmissions = 0;
    private int correctSubmissions = 0;
    private boolean isDaily = false;
    private Instant dailyDate;
    private boolean isPremium = false;
    private String topic;
    private Integer order;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    public static class Example {
        private String input;
        private String output;
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    public static class TestCase {
        private String stdin;
        private String expectedOutput;
        private boolean hidden;
    }
}

