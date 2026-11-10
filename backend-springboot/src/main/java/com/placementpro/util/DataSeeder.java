package com.placementpro.util;

import com.placementpro.model.*;
import com.placementpro.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedCompanies();
        seedProblems();
        seedRewards();
        seedAdminUser();
    }

    private void seedCompanies() {
        if (companyRepository.count() > 0) return;
        log.info("Seeding companies...");

        List<Company> companies = Arrays.asList(
            makeCompany("Google", "FAANG",
                Arrays.asList("Arrays", "Dynamic Programming", "Graphs", "System Design", "Trees"), "Hard", "40-80 LPA"),
            makeCompany("Amazon", "FAANG",
                Arrays.asList("Leadership Principles", "Arrays", "Trees", "System Design", "Graphs"), "Hard", "35-65 LPA"),
            makeCompany("Microsoft", "FAANG",
                Arrays.asList("Data Structures", "Algorithms", "OOP", "System Design"), "Medium", "30-60 LPA"),
            makeCompany("Meta", "FAANG",
                Arrays.asList("Arrays", "Strings", "Graphs", "Dynamic Programming"), "Hard", "45-90 LPA"),
            makeCompany("Flipkart", "Top MNC",
                Arrays.asList("DSA", "System Design", "DBMS", "OS"), "Medium", "20-40 LPA"),
            makeCompany("Infosys", "Service",
                Arrays.asList("Aptitude", "Basic DSA", "DBMS", "Verbal"), "Easy", "3.5-6 LPA"),
            makeCompany("TCS", "Service",
                Arrays.asList("Aptitude", "Verbal", "Basic Programming", "Email Writing"), "Easy", "3-5 LPA"),
            makeCompany("Wipro", "Service",
                Arrays.asList("Aptitude", "Basic DSA", "Communication", "DBMS"), "Easy", "3-5 LPA")
        );
        companyRepository.saveAll(companies);
        log.info("Seeded {} companies", companies.size());
    }

    private Company makeCompany(String name, String tier, List<String> topics, String difficulty, String avgPkg) {
        Company c = new Company();
        c.setName(name);
        c.setTier(tier);
        c.setRequiredTopics(topics);
        c.setDifficulty(difficulty);
        c.setAvgPackage(avgPkg);
        return c;
    }

    private void seedProblems() {
        if (problemRepository.count() > 0) return;
        log.info("Seeding problems...");

        List<Problem> problems = Arrays.asList(
            makeProblem("Two Sum", "two-sum",
                "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
                "Easy", Arrays.asList("Arrays", "Hashing"), "Arrays", 1,
                Arrays.asList(makeExample("nums = [2,7,11,15], target = 9", "[0,1]", "nums[0] + nums[1] == 9"))),

            makeProblem("Valid Parentheses", "valid-parentheses",
                "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.",
                "Easy", Arrays.asList("Strings", "Stack"), "Strings", 2,
                Arrays.asList(makeExample("s = \"()\"", "true", null), makeExample("s = \"()[]{}\"", "true", null))),

            makeProblem("Merge Two Sorted Lists", "merge-sorted-lists",
                "You are given the heads of two sorted linked lists list1 and list2. Merge the two lists in a sorted list.",
                "Easy", Arrays.asList("Linked Lists"), "Linked Lists", 3,
                Arrays.asList(makeExample("list1 = [1,2,4], list2 = [1,3,4]", "[1,1,2,3,4,4]", null))),

            makeProblem("Maximum Subarray", "maximum-subarray",
                "Given an integer array nums, find the subarray with the largest sum, and return its sum.",
                "Medium", Arrays.asList("Arrays", "Dynamic Programming"), "Arrays", 4,
                Arrays.asList(makeExample("nums = [-2,1,-3,4,-1,2,1,-5,4]", "6", "The subarray [4,-1,2,1] has the largest sum 6."))),

            makeProblem("Binary Search", "binary-search",
                "Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums.",
                "Easy", Arrays.asList("Arrays", "Binary Search"), "Arrays", 5,
                Arrays.asList(makeExample("nums = [-1,0,3,5,9,12], target = 9", "4", "9 exists in nums and its index is 4"))),

            makeProblem("Longest Substring Without Repeating Characters", "longest-substring",
                "Given a string s, find the length of the longest substring without repeating characters.",
                "Medium", Arrays.asList("Strings", "Sliding Window", "Hashing"), "Strings", 6,
                Arrays.asList(makeExample("s = \"abcabcbb\"", "3", "The answer is \"abc\", with the length of 3."))),

            makeProblem("Binary Tree Level Order Traversal", "bt-level-order",
                "Given the root of a binary tree, return the level order traversal of its nodes values.",
                "Medium", Arrays.asList("Trees", "BFS"), "Trees", 7,
                Arrays.asList(makeExample("root = [3,9,20,null,null,15,7]", "[[3],[9,20],[15,7]]", null))),

            makeProblem("Number of Islands", "number-of-islands",
                "Given an m x n 2D binary grid which represents a map of 1s (land) and 0s (water), return the number of islands.",
                "Medium", Arrays.asList("Graphs", "DFS", "BFS"), "Graphs", 8,
                Arrays.asList(makeExample("grid = [[\"1\",\"1\",\"0\"],[\"1\",\"1\",\"0\"],[\"0\",\"0\",\"1\"]]", "2", null))),

            makeProblem("Coin Change", "coin-change",
                "You are given an integer array coins representing coins of various denominations and an integer amount. Return the fewest number of coins needed.",
                "Medium", Arrays.asList("Dynamic Programming"), "Dynamic Programming", 9,
                Arrays.asList(makeExample("coins = [1,5,11], amount = 11", "1", null))),

            makeProblem("Median of Two Sorted Arrays", "median-sorted-arrays",
                "Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.",
                "Hard", Arrays.asList("Arrays", "Binary Search"), "Arrays", 10,
                Arrays.asList(makeExample("nums1 = [1,3], nums2 = [2]", "2.00000", null))),

            makeProblem("Trapping Rain Water", "trapping-rain-water",
                "Given n non-negative integers representing an elevation map, compute how much water it can trap after raining.",
                "Hard", Arrays.asList("Arrays", "Two Pointers", "Stack"), "Arrays", 11,
                Arrays.asList(makeExample("height = [0,1,0,2,1,0,1,3,2,1,2,1]", "6", null))),

            makeProblem("Word Break", "word-break",
                "Given a string s and a dictionary of strings wordDict, return true if s can be segmented into words from the dictionary.",
                "Medium", Arrays.asList("Dynamic Programming", "Strings", "Hashing"), "Dynamic Programming", 12,
                Arrays.asList(makeExample("s = \"leetcode\", wordDict = [\"leet\",\"code\"]", "true", null))),

            makeProblem("LRU Cache", "lru-cache",
                "Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.",
                "Medium", Arrays.asList("Design", "Hashing", "Linked Lists"), "Design", 13, List.of()),

            makeProblem("N-Queens", "n-queens",
                "The n-queens puzzle is the problem of placing n queens on an n x n chessboard such that no two queens attack each other.",
                "Hard", Arrays.asList("Recursion", "Backtracking"), "Recursion", 14, List.of()),

            makeProblem("Find Minimum in Rotated Sorted Array", "min-rotated",
                "Suppose an array of length n sorted in ascending order is rotated. Find the minimum element.",
                "Medium", Arrays.asList("Arrays", "Binary Search"), "Arrays", 15,
                Arrays.asList(makeExample("nums = [3,4,5,1,2]", "1", null)))
        );

        problemRepository.saveAll(problems);
        log.info("Seeded {} problems", problems.size());
    }

    private Problem makeProblem(String title, String slug, String desc, String difficulty,
                                 List<String> tags, String topic, int order, List<Problem.Example> examples) {
        Problem p = new Problem();
        p.setTitle(title);
        p.setSlug(slug);
        p.setDescription(desc);
        p.setDifficulty(difficulty);
        p.setTags(tags);
        p.setTopic(topic);
        p.setOrder(order);
        p.setExamples(examples);
        return p;
    }

    private Problem.Example makeExample(String input, String output, String explanation) {
        Problem.Example ex = new Problem.Example();
        ex.setInput(input);
        ex.setOutput(output);
        ex.setExplanation(explanation);
        return ex;
    }

    private void seedRewards() {
        if (rewardRepository.count() > 0) return;
        log.info("Seeding rewards...");

        List<Reward> rewards = Arrays.asList(
            makeReward("Resume Review Token", "Get expert feedback on your resume from industry professionals.", "resume_review", 200, "📄"),
            makeReward("Mock Interview Token", "Schedule a 1-on-1 mock interview session with a mentor.", "mock_token", 500, "🎙️"),
            makeReward("Google Premium Question Set", "Unlock 50+ exclusive Google interview questions with solutions.", "premium_set", 300, "🔍"),
            makeReward("Amazon Premium Question Set", "Unlock 50+ exclusive Amazon LP + technical questions.", "premium_set", 300, "📦"),
            makeReward("Gold Leaderboard Badge", "Show off your achievement on the leaderboard permanently.", "badge", 150, "🥇"),
            makeReward("Placement Certificate", "Official virtual certificate for completing placement preparation.", "certificate", 800, "🎓"),
            makeReward("Microsoft Premium Question Set", "Unlock 50+ exclusive Microsoft interview questions.", "premium_set", 300, "🪟"),
            makeReward("Contest Champion Badge", "Special badge for consistent contest participation.", "badge", 100, "🏆")
        );

        rewardRepository.saveAll(rewards);
        log.info("Seeded {} rewards", rewards.size());
    }

    private Reward makeReward(String title, String desc, String type, int cost, String icon) {
        Reward r = new Reward();
        r.setTitle(title);
        r.setDescription(desc);
        r.setType(type);
        r.setCreditCost(cost);
        r.setIcon(icon);
        r.setAvailable(true);
        return r;
    }

    private void seedAdminUser() {
        if (userRepository.findByEmail("admin@placementpro.com").isPresent()) return;
        log.info("Creating admin user...");

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@placementpro.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("admin");

        User.Profile profile = new User.Profile();
        profile.setFullName("PlacementPro Admin");
        admin.setProfile(profile);

        userRepository.save(admin);
        log.info("Admin user created: admin@placementpro.com / admin123");

        // Also create a demo student
        if (userRepository.findByEmail("student@placementpro.com").isPresent()) return;
        User student = new User();
        student.setUsername("demostudent");
        student.setEmail("student@placementpro.com");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setRole("student");
        student.setCredits(50);

        User.Profile sp = new User.Profile();
        sp.setFullName("Demo Student");
        sp.setCollege("Demo College");
        student.setProfile(sp);

        userRepository.save(student);
        log.info("Demo student created: student@placementpro.com / student123");
    }
}

