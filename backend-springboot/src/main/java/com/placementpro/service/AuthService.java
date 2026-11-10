package com.placementpro.service;

import com.placementpro.dto.AuthResponse;
import com.placementpro.dto.LoginRequest;
import com.placementpro.dto.RegisterRequest;
import com.placementpro.dto.UpdateProfileRequest;
import com.placementpro.exception.BadRequestException;
import com.placementpro.exception.ResourceNotFoundException;
import com.placementpro.model.Credit;
import com.placementpro.model.User;
import com.placementpro.repository.CreditRepository;
import com.placementpro.repository.UserRepository;
import com.placementpro.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User.Profile profile = new User.Profile();
        profile.setFullName(request.getFullName());
        profile.setCollege(request.getCollege());
        user.setProfile(profile);

        user.setCredits(50);
        user = userRepository.save(user);

        // Award welcome credits
        Credit credit = new Credit();
        credit.setUser(user.getId());
        credit.setAmount(50);
        credit.setType("earn");
        credit.setReason("Welcome bonus");
        credit.setBalance(50);
        creditRepository.save(credit);

        String token = jwtUtils.generateToken(user.getId());
        return new AuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // Daily login streak & credits
        Instant now = Instant.now();
        String today = now.truncatedTo(ChronoUnit.DAYS).toString();
        User.Stats stats = user.getStats();
        String lastActive = stats.getLastActive() != null
                ? stats.getLastActive().truncatedTo(ChronoUnit.DAYS).toString() : null;

        if (!today.equals(lastActive)) {
            String yesterday = now.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).toString();
            int newStreak = yesterday.equals(lastActive) ? stats.getStreak() + 1 : 1;
            int bonusCredits = (newStreak % 7 == 0) ? 50 : 5;

            stats.setLastActive(now);
            stats.setStreak(newStreak);
            stats.setLongestStreak(Math.max(newStreak, stats.getLongestStreak()));
            user.setStats(stats);
            user.setCredits(user.getCredits() + bonusCredits);

            Credit credit = new Credit();
            credit.setUser(user.getId());
            credit.setAmount(bonusCredits);
            credit.setType("earn");
            credit.setReason("Daily login" + (newStreak % 7 == 0 ? " + streak bonus" : ""));
            credit.setBalance(user.getCredits());
            creditRepository.save(credit);

            user = userRepository.save(user);
        }

        String token = jwtUtils.generateToken(user.getId());
        return new AuthResponse(user, token);
    }

    public User getProfile(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User.Profile profile = user.getProfile();
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getCollege() != null) profile.setCollege(request.getCollege());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLinkedIn() != null) profile.setLinkedIn(request.getLinkedIn());
        if (request.getGithub() != null) profile.setGithub(request.getGithub());
        if (request.getGraduationYear() != null) profile.setGraduationYear(request.getGraduationYear());
        user.setProfile(profile);

        if (request.getStudyHoursPerDay() != null) {
            User.Preferences prefs = user.getPreferences();
            prefs.setStudyHoursPerDay(request.getStudyHoursPerDay());
            user.setPreferences(prefs);
        }

        return userRepository.save(user);
    }
}

