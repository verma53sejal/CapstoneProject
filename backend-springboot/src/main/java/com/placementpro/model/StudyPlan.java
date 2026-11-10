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
@Document(collection = "studyplans")
public class StudyPlan {

    @Id
    private String id;

    private String user; // user ID
    private Integer weekNumber;
    private Instant startDate;
    private Instant endDate;

    private List<DailyTask> dailyTasks = new ArrayList<>();
    private List<String> targetCompanies = new ArrayList<>();
    private Instant generatedAt = Instant.now();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    public static class DailyTask {
        private String day;
        private Instant date;
        private List<Task> tasks = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class Task {
        private String topic;
        private String description;
        private List<String> problems = new ArrayList<>();
        private Integer estimatedHours;
        private boolean completed = false;
    }
}

