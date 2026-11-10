package com.placementpro.service;

import com.placementpro.exception.ResourceNotFoundException;
import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class StudyPlanService {

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Map<String, List<String>> TOPIC_ROADMAP = new LinkedHashMap<>();

    static {
        TOPIC_ROADMAP.put("Arrays", Arrays.asList("Two Pointers", "Sliding Window", "Prefix Sum"));
        TOPIC_ROADMAP.put("Strings", Arrays.asList("Pattern Matching", "Palindromes", "Anagrams"));
        TOPIC_ROADMAP.put("Linked Lists", Arrays.asList("Reversal", "Two Pointer", "Cycle Detection"));
        TOPIC_ROADMAP.put("Trees", Arrays.asList("DFS", "BFS", "Binary Search Tree"));
        TOPIC_ROADMAP.put("Graphs", Arrays.asList("BFS/DFS", "Shortest Path", "Union Find"));
        TOPIC_ROADMAP.put("Dynamic Programming", Arrays.asList("Memoization", "Tabulation", "Optimization"));
        TOPIC_ROADMAP.put("Sorting", Arrays.asList("Quick Sort", "Merge Sort", "Heap Sort"));
        TOPIC_ROADMAP.put("Recursion", Arrays.asList("Backtracking", "Divide and Conquer"));
    }

    public StudyPlan generatePlan(String userId, Integer studyHoursParam) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int studyHours = studyHoursParam != null ? studyHoursParam : user.getPreferences().getStudyHoursPerDay();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        List<String> topics = new ArrayList<>(TOPIC_ROADMAP.keySet());

        List<StudyPlan.DailyTask> dailyTasks = new ArrayList<>();
        for (int i = 0; i < days.length; i++) {
            String topic = topics.get(i % topics.size());
            List<String> subtopics = TOPIC_ROADMAP.get(topic);

            StudyPlan.Task task = new StudyPlan.Task();
            task.setTopic(topic);
            task.setDescription("Focus on " + topic + ": " + String.join(", ", subtopics));
            task.setEstimatedHours(studyHours);
            task.setCompleted(false);

            StudyPlan.DailyTask dailyTask = new StudyPlan.DailyTask();
            dailyTask.setDay(days[i]);
            dailyTask.setDate(Instant.now().plus(i, ChronoUnit.DAYS));
            dailyTask.setTasks(List.of(task));
            dailyTasks.add(dailyTask);
        }

        // Week number calculation
        Calendar cal = Calendar.getInstance();
        int weekNumber = cal.get(Calendar.WEEK_OF_YEAR);

        StudyPlan plan = new StudyPlan();
        plan.setUser(userId);
        plan.setWeekNumber(weekNumber);
        plan.setStartDate(Instant.now());
        plan.setEndDate(Instant.now().plus(7, ChronoUnit.DAYS));
        plan.setDailyTasks(dailyTasks);
        plan.setTargetCompanies(user.getTargetCompanies());
        plan.setGeneratedAt(Instant.now());

        return studyPlanRepository.save(plan);
    }

    public Optional<StudyPlan> getCurrentPlan(String userId) {
        return studyPlanRepository.findFirstByUserOrderByCreatedAtDesc(userId);
    }

    public StudyPlan updateTask(String planId, int dayIndex, int taskIndex, boolean completed) {
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));

        plan.getDailyTasks().get(dayIndex).getTasks().get(taskIndex).setCompleted(completed);
        return studyPlanRepository.save(plan);
    }
}

