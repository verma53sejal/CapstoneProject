package com.placementpro.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String role = "student";
    private Profile profile = new Profile();
    private Stats stats = new Stats();
    private int credits = 0;
    private double placementReadinessScore = 0;
    private String readinessLevel = "Beginner";
    private List<String> targetCompanies = new ArrayList<>();
    private boolean resumeUploaded = false;
    private double resumeScore = 0;
    private List<String> bookmarks = new ArrayList<>();
    private boolean dailyChallengeDone = false;
    private double aptitudeScore = 0;
    private Preferences preferences = new Preferences();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public Stats getStats() { return stats; }
    public void setStats(Stats stats) { this.stats = stats; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public double getPlacementReadinessScore() { return placementReadinessScore; }
    public void setPlacementReadinessScore(double placementReadinessScore) { this.placementReadinessScore = placementReadinessScore; }
    public String getReadinessLevel() { return readinessLevel; }
    public void setReadinessLevel(String readinessLevel) { this.readinessLevel = readinessLevel; }
    public List<String> getTargetCompanies() { return targetCompanies; }
    public void setTargetCompanies(List<String> targetCompanies) { this.targetCompanies = targetCompanies; }
    public boolean isResumeUploaded() { return resumeUploaded; }
    public void setResumeUploaded(boolean resumeUploaded) { this.resumeUploaded = resumeUploaded; }
    public double getResumeScore() { return resumeScore; }
    public void setResumeScore(double resumeScore) { this.resumeScore = resumeScore; }
    public List<String> getBookmarks() { return bookmarks; }
    public void setBookmarks(List<String> bookmarks) { this.bookmarks = bookmarks; }
    public boolean isDailyChallengeDone() { return dailyChallengeDone; }
    public void setDailyChallengeDone(boolean dailyChallengeDone) { this.dailyChallengeDone = dailyChallengeDone; }
    public double getAptitudeScore() { return aptitudeScore; }
    public void setAptitudeScore(double aptitudeScore) { this.aptitudeScore = aptitudeScore; }
    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    // ---- Inner Classes ----
    public static class Profile {
        private String fullName;
        private String avatar;
        private String college;
        private Integer graduationYear;
        private String bio;
        private String linkedIn;
        private String github;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }
        public Integer getGraduationYear() { return graduationYear; }
        public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getLinkedIn() { return linkedIn; }
        public void setLinkedIn(String linkedIn) { this.linkedIn = linkedIn; }
        public String getGithub() { return github; }
        public void setGithub(String github) { this.github = github; }
    }

    public static class Stats {
        private int easySolved = 0;
        private int mediumSolved = 0;
        private int hardSolved = 0;
        private int totalSolved = 0;
        private int streak = 0;
        private int longestStreak = 0;
        private Instant lastActive = Instant.now();
        private int contestsParticipated = 0;
        private int mockInterviewsDone = 0;

        public int getEasySolved() { return easySolved; }
        public void setEasySolved(int easySolved) { this.easySolved = easySolved; }
        public int getMediumSolved() { return mediumSolved; }
        public void setMediumSolved(int mediumSolved) { this.mediumSolved = mediumSolved; }
        public int getHardSolved() { return hardSolved; }
        public void setHardSolved(int hardSolved) { this.hardSolved = hardSolved; }
        public int getTotalSolved() { return totalSolved; }
        public void setTotalSolved(int totalSolved) { this.totalSolved = totalSolved; }
        public int getStreak() { return streak; }
        public void setStreak(int streak) { this.streak = streak; }
        public int getLongestStreak() { return longestStreak; }
        public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
        public Instant getLastActive() { return lastActive; }
        public void setLastActive(Instant lastActive) { this.lastActive = lastActive; }
        public int getContestsParticipated() { return contestsParticipated; }
        public void setContestsParticipated(int contestsParticipated) { this.contestsParticipated = contestsParticipated; }
        public int getMockInterviewsDone() { return mockInterviewsDone; }
        public void setMockInterviewsDone(int mockInterviewsDone) { this.mockInterviewsDone = mockInterviewsDone; }
    }

    public static class Preferences {
        private int studyHoursPerDay = 2;
        private boolean notifications = true;

        public int getStudyHoursPerDay() { return studyHoursPerDay; }
        public void setStudyHoursPerDay(int studyHoursPerDay) { this.studyHoursPerDay = studyHoursPerDay; }
        public boolean isNotifications() { return notifications; }
        public void setNotifications(boolean notifications) { this.notifications = notifications; }
    }
}
