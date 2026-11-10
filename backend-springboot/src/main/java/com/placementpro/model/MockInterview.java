package com.placementpro.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "mockinterviews")
public class MockInterview {

    @Id
    private String id;

    private String user; // user ID
    private String type = "Mixed"; // DSA | HR | Aptitude | Mixed

    private List<Question> questions = new ArrayList<>();
    private int score = 0;
    private int maxScore = 100;
    private Integer percentage;
    private Integer duration;
    private String feedback;
    private boolean completed = false;
    private Instant startTime;
    private Instant endTime;

    @CreatedDate
    private Instant createdAt;

    @Data
    @NoArgsConstructor
    public static class Question {
        private String question;
        private String type; // MCQ | Coding | HR
        private List<String> options = new ArrayList<>();
        private String correctAnswer;
        private String userAnswer;
        private Boolean isCorrect;
        private Long timeTaken;
    }
}

