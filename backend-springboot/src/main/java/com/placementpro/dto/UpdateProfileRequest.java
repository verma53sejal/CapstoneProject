package com.placementpro.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String college;
    private String bio;
    private String linkedIn;
    private String github;
    private Integer graduationYear;
    private Integer studyHoursPerDay;
}

