package com.placementpro.service;

import com.placementpro.exception.BadRequestException;
import com.placementpro.exception.ResourceNotFoundException;
import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MockInterviewService {

    @Autowired
    private MockInterviewRepository mockInterviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditRepository creditRepository;

    private static final List<MockInterview.Question> MCQ_QUESTIONS = Arrays.asList(
        createMCQ("What is the time complexity of binary search?",
                  Arrays.asList("O(n)", "O(log n)", "O(n²)", "O(1)"), "O(log n)"),
        createMCQ("Which data structure uses LIFO principle?",
                  Arrays.asList("Queue", "Stack", "Heap", "Tree"), "Stack"),
        createMCQ("What does HTTP stand for?",
                  Arrays.asList("HyperText Transfer Protocol", "High Transfer Text Protocol",
                                "Hyper Transfer Tech Protocol", "None"), "HyperText Transfer Protocol"),
        createMCQ("Which sorting algorithm has O(n log n) average case?",
                  Arrays.asList("Bubble Sort", "Insertion Sort", "Merge Sort", "Selection Sort"), "Merge Sort"),
        createMCQ("What is a primary key in a database?",
                  Arrays.asList("Foreign key reference", "Unique identifier for a record",
                                "Index on a column", "None of the above"), "Unique identifier for a record")
    );

    private static final List<MockInterview.Question> HR_QUESTIONS = Arrays.asList(
        createHR("Tell me about yourself."),
        createHR("Where do you see yourself in 5 years?"),
        createHR("What is your greatest strength?"),
        createHR("Why do you want to work for this company?"),
        createHR("Describe a challenge you faced and how you overcame it.")
    );

    private static final List<MockInterview.Question> CODING_QUESTIONS = Arrays.asList(
        createCoding("Write a function to reverse a string."),
        createCoding("Find the maximum element in an array."),
        createCoding("Check if a number is prime.")
    );

    private static MockInterview.Question createMCQ(String question, List<String> options, String correctAnswer) {
        MockInterview.Question q = new MockInterview.Question();
        q.setQuestion(question);
        q.setType("MCQ");
        q.setOptions(options);
        q.setCorrectAnswer(correctAnswer);
        return q;
    }

    private static MockInterview.Question createHR(String question) {
        MockInterview.Question q = new MockInterview.Question();
        q.setQuestion(question);
        q.setType("HR");
        return q;
    }

    private static MockInterview.Question createCoding(String question) {
        MockInterview.Question q = new MockInterview.Question();
        q.setQuestion(question);
        q.setType("Coding");
        return q;
    }

    public MockInterview startInterview(String userId, String type) {
        List<MockInterview.Question> questions = new ArrayList<>();
        if ("Mixed".equals(type) || "DSA".equals(type)) {
            questions.addAll(MCQ_QUESTIONS.subList(0, 3));
            questions.addAll(CODING_QUESTIONS.subList(0, 2));
        } else if ("HR".equals(type)) {
            questions.addAll(HR_QUESTIONS);
        } else if ("Aptitude".equals(type)) {
            questions.addAll(MCQ_QUESTIONS);
        }

        MockInterview interview = new MockInterview();
        interview.setUser(userId);
        interview.setType(type);
        interview.setQuestions(questions);
        interview.setMaxScore(questions.size() * 10);
        interview.setStartTime(Instant.now());
        return mockInterviewRepository.save(interview);
    }

    public MockInterview submitInterview(String interviewId, String userId, List<String> answers) {
        MockInterview interview = mockInterviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getUser().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }

        int score = 0;
        List<MockInterview.Question> updatedQuestions = new ArrayList<>();
        List<MockInterview.Question> originalQuestions = interview.getQuestions();

        for (int i = 0; i < originalQuestions.size(); i++) {
            MockInterview.Question q = originalQuestions.get(i);
            String answer = (answers != null && i < answers.size()) ? answers.get(i) : null;
            boolean isCorrect = "MCQ".equals(q.getType()) && answer != null && answer.equals(q.getCorrectAnswer());
            if (isCorrect) score += 10;

            MockInterview.Question updated = new MockInterview.Question();
            updated.setQuestion(q.getQuestion());
            updated.setType(q.getType());
            updated.setOptions(q.getOptions());
            updated.setCorrectAnswer(q.getCorrectAnswer());
            updated.setUserAnswer(answer);
            updated.setIsCorrect(isCorrect);
            updatedQuestions.add(updated);
        }

        int percentage = interview.getMaxScore() > 0
                ? Math.round((float) score / interview.getMaxScore() * 100) : 0;
        String feedback;
        if (percentage >= 80) feedback = "Excellent performance! Keep it up.";
        else if (percentage >= 60) feedback = "Good effort! Review weak areas.";
        else feedback = "Needs improvement. Focus on fundamentals.";

        interview.setQuestions(updatedQuestions);
        interview.setScore(score);
        interview.setPercentage(percentage);
        interview.setFeedback(feedback);
        interview.setCompleted(true);
        interview.setEndTime(Instant.now());
        if (interview.getStartTime() != null) {
            interview.setDuration((int) (Instant.now().getEpochSecond() - interview.getStartTime().getEpochSecond()));
        }
        mockInterviewRepository.save(interview);

        // Award credits
        User user = userRepository.findById(userId).orElseThrow();
        user.setCredits(user.getCredits() + 50);
        User.Stats stats = user.getStats();
        stats.setMockInterviewsDone(stats.getMockInterviewsDone() + 1);
        user.setStats(stats);
        userRepository.save(user);

        Credit credit = new Credit();
        credit.setUser(userId);
        credit.setAmount(50);
        credit.setType("earn");
        credit.setReason("Mock interview completed");
        credit.setBalance(user.getCredits());
        creditRepository.save(credit);

        return interview;
    }

    public List<MockInterview> getHistory(String userId) {
        return mockInterviewRepository.findByUserAndCompletedOrderByCreatedAtDesc(userId, true);
    }
}

